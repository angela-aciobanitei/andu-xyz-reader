package com.ang.acb.materialme.ui.details;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticleDetailsBinding;
import com.ang.acb.materialme.ui.common.ArticlesViewModel;
import com.ang.acb.materialme.ui.common.MainActivity;
import com.ang.acb.materialme.utils.InjectorUtils;
import com.ang.acb.materialme.utils.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

public class ArticleDetailsFragment extends Fragment {

    private static final String ARG_POSITION = "ARG_POSITION";

    private FragmentArticleDetailsBinding binding;
    private ArticlesViewModel viewModel;
    private int position;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        ViewModelFactory viewModelFactory = InjectorUtils.provideViewModelFactory(getActivity());
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticlesViewModel.class);
        Timber.d("Setup article details view model.");
    }

    private void observeArticleDetails() {
        viewModel.getArticleListLiveData().observe(
                getViewLifecycleOwner(),
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        Timber.d("Observe article list.");
                        if (resource != null && resource.data != null) {
                            binding.setArticle(resource.data.get(position));
                        }
                    }
        });
    }
}