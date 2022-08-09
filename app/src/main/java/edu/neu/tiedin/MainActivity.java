package edu.neu.tiedin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.cache.normalized.NormalizedCache;
import com.apollographql.apollo3.cache.normalized.api.FieldPolicyCacheResolver;
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory;
import com.apollographql.apollo3.cache.normalized.api.TypePolicyCacheKeyGenerator;
import com.apollographql.apollo3.rx3.Rx3Apollo;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import edu.neu.tiedin.databinding.ActivityMainBinding;
import edu.neu.tiedin.type.Point;
import io.reactivex.rxjava3.core.Single;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String SHARED_PREFS = "LOGIN_PREFS";
    public static final String USER_KEY = "USER_SESSION_KEY";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    SharedPreferences sharedpreferences;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_plan_trip)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}