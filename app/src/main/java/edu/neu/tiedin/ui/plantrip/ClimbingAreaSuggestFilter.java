package edu.neu.tiedin.ui.plantrip;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.neu.tiedin.AreasByFilterQuery;
import edu.neu.tiedin.R;
import edu.neu.tiedin.type.AreaFilter;
import io.reactivex.rxjava3.core.Single;

class ClimbingAreaSuggestFilter extends ArrayAdapter<String> implements Filterable {

    private final String TAG = "ClimbingAreaSuggestFilter";
    private ArrayList<String> data;
    ApolloClient client;
    Filter filter;

    public ClimbingAreaSuggestFilter(@NonNull Context context, int resource) {
        super(context, resource);
        data = new ArrayList<>();
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
                    data.clear();
                    data.addAll(dataApolloResponse.data.areas.stream().map(area -> area.areaName).collect(Collectors.toList()));

                    results.values = data;
                    results.count = data.size();
                });

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
        };
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }
}
