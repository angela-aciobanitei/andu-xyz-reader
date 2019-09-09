package com.ang.acb.materialme.ui.list;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticleListBinding;
import com.ang.acb.materialme.ui.common.ArticlesViewModel;
import com.ang.acb.materialme.ui.common.MainActivity;
import com.ang.acb.materialme.utils.InjectorUtils;
import com.ang.acb.materialme.utils.SpacingItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

import static com.ang.acb.materialme.ui.details.ArticlesViewPagerFragment.ARG_POSITION;

public class ArticleListFragment extends Fragment {

    private FragmentArticleListBinding binding;
    private ArticlesViewModel viewModel;
    private ArticlesAdapter adapter;

    // Required empty public constructor
    public ArticleListFragment() {}


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentArticleListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        setupAdapter();
        initViewModel();
        populateUi();
    }

    private MainActivity getHostActivity() {
        return (MainActivity)getActivity();
    }

    private void setupToolbar(){
        getHostActivity().setSupportActionBar(binding.mainToolbar);
        if(getHostActivity().getSupportActionBar() != null) {
            // The activity title/subtitle should not be displayed.
            getHostActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initViewModel() {
        viewModel = InjectorUtils.provideViewModel(getHostActivity());
        Timber.d("Setup articles view model.");
    }

    private void setupRecyclerView(){
        binding.rvArticleList.setLayoutManager(new GridLayoutManager(
                getHostActivity(), getResources().getInteger(R.integer.grid_column_count)));
        binding.rvArticleList.addItemDecoration(
                new SpacingItemDecoration(getHostActivity(), R.dimen.item_offset));
        Timber.d("Setup articles recycle view.");
    }

    private void setupAdapter() {
        adapter =  new ArticlesAdapter(this::onArticleClicked);
        binding.rvArticleList.setAdapter(adapter);
        Timber.d("Setup article list adapter.");
    }

    private void onArticleClicked(int position) {
        Bundle bundle = new Bundle() ;
        bundle.putInt(ARG_POSITION, position);
        NavHostFragment.findNavController(ArticleListFragment.this)
                .navigate(R.id.action_article_list_to_articles_view_pager,
                        bundle, null, null);
    }

    private void populateUi() {
        viewModel.getArticleListLiveData().observe(
                getViewLifecycleOwner(),
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        Timber.d("Observe article list.");
                        if (resource != null && resource.data != null) {
                            adapter.submitList(resource.data);
                        }
                    }
                });
    }

}
