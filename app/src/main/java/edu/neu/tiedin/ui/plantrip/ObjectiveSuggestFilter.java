package edu.neu.tiedin.ui.plantrip;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.Operation;
import com.apollographql.apollo3.cache.normalized.NormalizedCache;
import com.apollographql.apollo3.cache.normalized.api.FieldPolicyCacheResolver;
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory;
import com.apollographql.apollo3.cache.normalized.api.TypePolicyCacheKeyGenerator;
import com.apollographql.apollo3.rx3.Rx3Apollo;

import org.reactivestreams.Subscriber;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.neu.tiedin.AreaByUUIDQuery;
import edu.neu.tiedin.AreasByFilterQuery;
import edu.neu.tiedin.R;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableConcatArray;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableConcatMap;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlinx.coroutines.flow.Flow;

public class ObjectiveSuggestFilter extends ArrayAdapter<AreasByFilterQuery.Area> implements Filterable {

    private final String TAG = "ObjectiveSuggestFilter";

    private List<AreasByFilterQuery.Area> areasToSearch;

    private ArrayList<AreaByUUIDQuery.Climb> climbList;
    private ArrayList<AreaByUUIDQuery.Climb> tempClimbList;
    ApolloClient client;
    Filter filter;
    Context context;

    public ObjectiveSuggestFilter(@NonNull Context context, int resource, List<AreasByFilterQuery.Area> areasToSearch) {
        super(context, resource);
        this.context = context;
        this.areasToSearch = areasToSearch;

        climbList = new ArrayList<>();
        tempClimbList = new ArrayList<>();
        ApolloClient.Builder builder = new ApolloClient.Builder()
                .serverUrl(context.getString(R.string.OPENBETA_ENDPOINT_ADDRESS));


        // Optionally, set a normalized cache
        NormalizedCache.configureApolloClientBuilder(
                builder,
                new MemoryCacheFactory(10 * 1024 * 1024, -1),
                TypePolicyCacheKeyGenerator.INSTANCE,
                FieldPolicyCacheResolver.INSTANCE,
                false
        );

        client = builder.build();

        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null) {
                    return results;
                }

                // Query each area (crag) by UUID in order to collect all of their climbs for the search
                List<ApolloCall<AreaByUUIDQuery.Data>> cragByUUIDQueries = areasToSearch.stream().map(
                        area -> client.query(new AreaByUUIDQuery(area.uuid))).collect(Collectors.toList());

                // Flow all requests in parallel
                Flowable<ApolloCall<AreaByUUIDQuery.Data>> parallelRequest = new FlowableConcatArray<ApolloCall<AreaByUUIDQuery.Data>>(cragByUUIDQueries)
                        .parallel()
                        .runOn(Schedulers.io());


                // Always clear
                tempClimbList.clear();
                Rx3Apollo.flowable(parallelRequest).blockingSubscribe(genericResponse -> {
                    ApolloResponse<AreaByUUIDQuery.Data> dataApolloResponse = (ApolloResponse<AreaByUUIDQuery.Data>) genericResponse;

                    // Only try to update if there are actual results
                    if (!dataApolloResponse.hasErrors() &&
                            dataApolloResponse.data.area.climbs != null &&
                            dataApolloResponse.data.area.climbs.size() > 0) {
                        tempClimbList.addAll(dataApolloResponse.data.area.climbs);
                    }

                    results.values = tempClimbList;
                    results.count = tempClimbList.size();
                });

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    climbList.clear();
                    climbList.addAll(tempClimbList);
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue == null) {
                    return "null";
                } else if (!(resultValue instanceof AreasByFilterQuery.Area)) {
                    return "null";
                } else {
                    return ((AreasByFilterQuery.Area) resultValue).areaName;
                }
            }
        };
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2,parent,false);

        AreasByFilterQuery.Area currentArea = areasList.get(position);

        TextView name = (TextView) listItem.findViewById(android.R.id.text1);
        TextView subtext = (TextView) listItem.findViewById(android.R.id.text2);
        name.setText(currentArea.areaName);

        DecimalFormat df = new DecimalFormat("###.#####");
        subtext.setText(df.format(currentArea.metadata.lat)
                + ", " + df.format(currentArea.metadata.lng));

        return listItem;
    }

    @Override
    public int getCount() {
        return areasList.size();
    }

    @Override
    public AreasByFilterQuery.Area getItem(int position) {
        return areasList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }
}
