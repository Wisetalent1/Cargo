package com.nicargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.nicargo.app.R;
import com.nicargo.app.adapters.StatsCardAdapter;
import com.nicargo.app.models.Stats;
import com.nicargo.app.models.StatsCardItem;
import com.nicargo.app.utils.AuthTokenManager;
import com.nicargo.app.viewmodels.DashboardViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    private DashboardViewModel dashboardViewModel;
    private AuthTokenManager tokenManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView statsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private TextView lastUpdatedText;
    private TextView userNameText;
    private StatsCardAdapter statsAdapter;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tokenManager = AuthTokenManager.getInstance(this);
        
        // Check if logged in
        if (!tokenManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupSwipeRefresh();
        loadDashboardStats();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        statsRecyclerView = findViewById(R.id.statsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);
        lastUpdatedText = findViewById(R.id.lastUpdatedText);
        userNameText = findViewById(R.id.userName);
        scrollView = findViewById(R.id.scrollView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
        }
    }

    private void setupRecyclerView() {
        statsAdapter = new StatsCardAdapter(this);
        statsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        statsRecyclerView.setAdapter(statsAdapter);
        statsRecyclerView.setHasFixedSize(true);
    }

    private void setupViewModel() {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        dashboardViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                statsRecyclerView.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                statsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        dashboardViewModel.getStats().observe(this, stats -> {
            if (stats != null) {
                updateStats(stats);
                updateLastUpdated();
                emptyStateText.setVisibility(View.GONE);
                statsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                emptyStateText.setVisibility(View.VISIBLE);
                statsRecyclerView.setVisibility(View.GONE);
            }
        });

        dashboardViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                emptyStateText.setText(error);
                emptyStateText.setVisibility(View.VISIBLE);
                statsRecyclerView.setVisibility(View.GONE);
            }
        });

        dashboardViewModel.getIsUnauthorized().observe(this, isUnauthorized -> {
            if (isUnauthorized != null && isUnauthorized) {
                navigateToLogin();
            }
        });

        dashboardViewModel.getUserName().observe(this, name -> {
            if (name != null && !name.isEmpty()) {
                userNameText.setText(name);
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadDashboardStats);
        swipeRefreshLayout.setColorSchemeResources(
            R.color.primary,
            R.color.accent,
            R.color.primary_dark
        );
    }

    private void loadDashboardStats() {
        dashboardViewModel.loadDashboardStats();
    }

    private void updateStats(Stats stats) {
        List<StatsCardItem> cardItems = new ArrayList<>();
        
        // Total Shipments
        cardItems.add(new StatsCardItem(
            "Total Shipments",
            String.valueOf(stats.getTotalShipments()),
            "All time shipments",
            R.drawable.ic_shipment,
            R.color.primary,
            R.color.primary_light_bg
        ));
        
        // Total Revenue
        cardItems.add(new StatsCardItem(
            "Total Revenue",
            "$" + String.format("%.2f", stats.getTotalRevenue()),
            "All time revenue",
            R.drawable.ic_revenue,
            R.color.accent,
            R.color.accent_light_bg
        ));
        
        // Pending Shipments
        cardItems.add(new StatsCardItem(
            "Pending",
            String.valueOf(stats.getPendingShipments()),
            "Awaiting delivery",
            R.drawable.ic_pending,
            R.color.error_red,
            R.color.error_light_bg
        ));
        
        // Delivered Shipments
        cardItems.add(new StatsCardItem(
            "Delivered",
            String.valueOf(stats.getDeliveredShipments()),
            "Completed deliveries",
            R.drawable.ic_delivered,
            R.color.success_green,
            R.color.success_light_bg
        ));
        
        statsAdapter.setStats(cardItems);
    }

    private void updateLastUpdated() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = sdf.format(new Date());
        lastUpdatedText.setText("Last updated: " + time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        dashboardViewModel.logout();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
