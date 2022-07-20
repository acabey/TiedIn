package edu.neu.tiedin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.cache.normalized.NormalizedCache;
import com.apollographql.apollo3.cache.normalized.api.FieldPolicyCacheResolver;
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory;
import com.apollographql.apollo3.cache.normalized.api.TypePolicyCacheKeyGenerator;

import edu.neu.tiedin.type.Point;
import graphql.com.google.common.base.Optional;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApolloClient.Builder builder = new ApolloClient.Builder()
                .serverUrl("https://api.openbeta.io/");


        // Optionally, set a normalized cache
        NormalizedCache.configureApolloClientBuilder(
                builder,
                new MemoryCacheFactory(10 * 1024 * 1024, -1),
                TypePolicyCacheKeyGenerator.INSTANCE,
                FieldPolicyCacheResolver.INSTANCE,
                false
        );

        ApolloClient client = builder.build();
        client.query(new CragsNearQuery("Example", new Point(Optional.of(42.24738820721922), -71.32416137320287),0, 1600*50,true)).execute();

    }

}