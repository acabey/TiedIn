package edu.neu.tiedin.ui.plantrip;

import android.content.Context;
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
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.cache.normalized.NormalizedCache;
import com.apollographql.apollo3.cache.normalized.api.FieldPolicyCacheResolver;
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory;
import com.apollographql.apollo3.cache.normalized.api.TypePolicyCacheKeyGenerator;
import com.apollographql.apollo3.rx3.Rx3Apollo;

import java.util.ArrayList;

import edu.neu.tiedin.AreasByFilterQuery;
import edu.neu.tiedin.R;
import edu.neu.tiedin.type.AreaFilter;
import io.reactivex.rxjava3.core.Single;

class ClimbingAreaSuggestFilter extends ArrayAdapter<AreasByFilterQuery.Area> implements Filterable {

    private final String TAG = "ClimbingAreaSuggestFilter";
    private ArrayList<AreasByFilterQuery.Area> areasList;
    private ArrayList<AreasByFilterQuery.Area> tempAreasList;
    ApolloClient client;
    Filter filter;
    Context context;

    public ClimbingAreaSuggestFilter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
        areasList = new ArrayList<>();
        tempAreasList = new ArrayList<>();
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

                // Partial match Area name with given search term
                ApolloCall<AreasByFilterQuery.Data> cragsByName = client.query(new AreasByFilterQuery(new edu.neu.tiedin.type.Filter(
                        new Optional.Present<>(new AreaFilter(constraint.toString(), new Optional.Present<>(false))),
                        Optional.Absent.INSTANCE,
                        Optional.Absent.INSTANCE,
                        Optional.Absent.INSTANCE
                )));

                Single<ApolloResponse<AreasByFilterQuery.Data>> queryResponse = Rx3Apollo.single(cragsByName);

                queryResponse.blockingSubscribe(dataApolloResponse -> {

                    // Always clear
                    tempAreasList.clear();

                    // Only try to update if there are actual results
                    if (!dataApolloResponse.hasErrors() &&
                            dataApolloResponse.data.areas != null &&
                            dataApolloResponse.data.areas.size() > 0) {
                        tempAreasList.addAll(dataApolloResponse.data.areas);
                    }

                    results.values = tempAreasList;
                    results.count = tempAreasList.size();
                });

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    areasList.clear();
                    areasList.addAll(tempAreasList);
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
            listItem = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,parent,false);

        AreasByFilterQuery.Area currentArea = areasList.get(position);

        TextView name = (TextView) listItem.findViewById(android.R.id.text1);
        name.setText(currentArea.areaName);

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
