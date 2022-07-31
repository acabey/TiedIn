package edu.neu.tiedin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.ExecutionContext;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.cache.normalized.NormalizedCache;
import com.apollographql.apollo3.cache.normalized.api.FieldPolicyCacheResolver;
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory;
import com.apollographql.apollo3.cache.normalized.api.TypePolicyCacheKeyGenerator;
import com.apollographql.apollo3.rx3.Rx3Apollo;

import edu.neu.tiedin.type.Point;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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
//        client.query(new CragsNearQuery(new Optional.Present<>("Example"),
//                new Optional.Present<>(new Point(new Optional.Present<>(42.24738820721922), new Optional.Present<>(-71.32416137320287))),
//                new Optional.Present<>(0),
//                new Optional.Present<>(1600*50),
//                new Optional.Present<>(true))).execute();


        ApolloCall<CragsNearQuery.Data> cragsNearBoston = client.query(new CragsNearQuery("Example",
                new Point(new Optional.Present<>(42.24738820721922), new Optional.Present<>(-71.32416137320287)),
                0,
                1600*50,
                true));

        Single<ApolloResponse<CragsNearQuery.Data>> queryResponse = Rx3Apollo.single(cragsNearBoston);

        queryResponse.subscribe(new Consumer<ApolloResponse<CragsNearQuery.Data>>() {
            @Override
            public void accept(ApolloResponse<CragsNearQuery.Data> dataApolloResponse) throws Throwable {
                Log.d(TAG, "accept: size " + dataApolloResponse.data.cragsNear.size());
            }
        });

    }

}