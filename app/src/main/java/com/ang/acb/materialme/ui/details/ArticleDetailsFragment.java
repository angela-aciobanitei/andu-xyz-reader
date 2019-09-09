package com.ang.acb.materialme.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticleDetailsBinding;
import com.ang.acb.materialme.ui.viewmodel.ArticlesViewModel;
import com.ang.acb.materialme.ui.common.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

public class ArticleDetailsFragment extends Fragment {

    private static final String ARG_POSITION = "ARG_POSITION";

    private FragmentArticleDetailsBinding binding;
    private ArticlesViewModel viewModel;
    private int position;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public ArticleDetailsFragment() {}

    public static ArticleDetailsFragment newInstance(int position) {
        ArticleDetailsFragment fragment = new ArticleDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        // Note: when using Dagger for injecting Fragment objects, inject as early as possible.
        // For this reason, call AndroidInjection.inject() in onAttach(). This also prevents
        // inconsistencies if the Fragment is reattached.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentArticleDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupToolbar();
        setupShareFab();
        initViewModel();
        observeArticleDetails();

    }

    private void setupToolbar() {
        ((MainActivity)getActivity()).setSupportActionBar(binding.detailsToolbar);
        if (((MainActivity)getActivity()).getSupportActionBar() != null) {
            // Handle Up navigation and hide title
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        binding.detailsToolbar.setNavigationOnClickListener(view ->
                // Attempts to navigate up in the navigation hierarchy.
                NavHostFragment.findNavController(
                        ArticleDetailsFragment.this).navigateUp());
    }

    private void setupShareFab() {
        binding.detailsShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticlesViewModel.class);
        Timber.d("Setup articles view model.");
    }

    private void observeArticleDetails() {
        viewModel.getArticleListLiveData().observe(
                getViewLifecycleOwner(),
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        Timber.d("Observe article item.");
                        if (resource != null && resource.data != null) {
                            binding.setArticle(resource.data.get(position));
                        }
                    }
        });
    }
}
