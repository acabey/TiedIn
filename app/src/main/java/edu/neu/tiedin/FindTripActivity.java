package edu.neu.tiedin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.cache.normalized.NormalizedCache;
import com.apollographql.apollo3.cache.normalized.api.FieldPolicyCacheResolver;
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory;
import com.apollographql.apollo3.cache.normalized.api.TypePolicyCacheKeyGenerator;
import com.apollographql.apollo3.rx3.Rx3Apollo;

import edu.neu.tiedin.type.Point;
import io.reactivex.rxjava3.core.Single;

public class FindTripActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String SHARED_PREFS = "LOGIN_PREFS";
    public static final String USER_KEY = "USER_SESSION_KEY";

    SharedPreferences sharedpreferences;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trip);

        // Get login preferences (if they exist)
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        email = sharedpreferences.getString(USER_KEY, null);

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

        ApolloCall<CragsNearQuery.Data> cragsNearBoston = client.query(new CragsNearQuery("Example",
                new Point(new Optional.Present<>(42.24738820721922), new Optional.Present<>(-71.32416137320287)),
                0,
                1600*50,
                true));

        Single<ApolloResponse<CragsNearQuery.Data>> queryResponse = Rx3Apollo.single(cragsNearBoston);

        queryResponse.subscribe(dataApolloResponse -> Log.d(TAG, "accept: size " + dataApolloResponse.data.cragsNear.size()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        forceLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        forceLogin();
    }

    private void forceLogin() {
        // If there is an existing session, transition to Main
        if (email == null) {
            Log.d(TAG, "forceLogin: email not found, forcing login screen");
            Intent i = new Intent(FindTripActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

}