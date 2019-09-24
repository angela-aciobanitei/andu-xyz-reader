package com.ang.acb.materialme.ui.grid;


import android.content.Context;
import android.os.Bundle;
;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticleGridBinding;
import com.ang.acb.materialme.ui.viewmodel.ArticlesViewModel;
import com.ang.acb.materialme.ui.common.MainActivity;
import com.ang.acb.materialme.utils.GridMarginDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static android.widget.GridLayout.VERTICAL;

/**
 * A fragment that displays a grid of article items.
 *
 * See: https://github.com/android/animation-samples/tree/master/GridToPager
 * See: https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html
 */
public class ArticleGridFragment extends Fragment {

    private FragmentArticleGridBinding binding;
    private ArticlesViewModel viewModel;
    private ArticlesAdapter adapter;
    private ViewHolderListener listener;
    private AtomicBoolean isEnterTransitionStarted;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public ArticleGridFragment() {}

    @Override
    public void onAttach(@NotNull Context context) {
        // Note: when using Dagger for injecting Fragment objects,
        // inject as early as possible. For this reason, call
        // AndroidInjection.inject() in onAttach(). This also prevents
        // inconsistencies if the Fragment is reattached.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentArticleGridBinding.inflate(inflater, container, false);

        setupToolbar();
        setupRecyclerView();
        initViewModel();
        prepareTransitions();
        setupViewHolderListener();
        setupAdapter();
        populateUi();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
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
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticlesViewModel.class);
        Timber.d("Setup articles view model.");
    }

    private void setupRecyclerView(){
        binding.articlesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.grid_column_count), VERTICAL));
        binding.articlesRecyclerView.addItemDecoration(
                new GridMarginDecoration(getHostActivity(), R.dimen.item_offset));
        Timber.d("Setup articles recycle view.");
    }

    /**
     * Prepares the shared element transition to the pager fragment,
     * as well as the other transitions that affect the flow.
     */
    private void prepareTransitions() {
        isEnterTransitionStarted = new AtomicBoolean();
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition));

        // Note: a similar mapping is set in ArticlesPagerFragment
        // with a setEnterSharedElementCallback().
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // Locate the ViewHolder for the clicked position.
                RecyclerView.ViewHolder selectedViewHolder = binding.articlesRecyclerView
                        .findViewHolderForAdapterPosition(viewModel.getCurrentPosition());
                if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                    return;
                }

                // Map the first shared element name to the child ImageView.
                ImageView childImageView = selectedViewHolder.itemView
                        .findViewById(R.id.article_item_thumbnail);
                sharedElements.put(names.get(0),childImageView);
            }
        });
    }

    private void setupViewHolderListener() {
        listener = new ViewHolderListener() {
            @Override
            public void onItemClicked(View sharedView, String sharedElementName, int position) {
                // Save current position to view model.
                viewModel.setCurrentPosition(position);

                // Create the shared element transition extras.
                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(sharedView, sharedElementName)
                        .build();

                // Navigate to destination (ArticlesPagerFragment), passing in the
                // current position as arg and the shared elements as extras.
                NavHostFragment.findNavController(ArticleGridFragment.this)
                        .navigate(R.id.action_article_list_to_articles_view_pager,
                                null, null, extras);
            }

            @Override
            public void onLoadCompleted(ImageView view, int position) {
                // Call startPostponedEnterTransition() only when the
                // selected image loading is completed.
                if (viewModel.getCurrentPosition() != position) return;
                if (isEnterTransitionStarted.getAndSet(true)) return;
                scheduleStartPostponedTransition();
            }
        };
    }

    private void scheduleStartPostponedTransition() {
        // Before calling startPostponedEnterTransition(), make sure that
        // the view is drawn first using ViewTreeObserver's OnPreDrawListener.
        // https://medium.com/@ayushkhare/shared-element-transitions-4a645a30c848
        binding.articlesRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        binding.articlesRecyclerView.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        // TODO Start postponed enter transition.
                        startPostponedEnterTransition();
                        return true;
                    }
                }
        );
    }

    private void setupAdapter() {
        adapter =  new ArticlesAdapter(listener);
        binding.articlesRecyclerView.setAdapter(adapter);
        Timber.d("Setup article list adapter.");
    }

    private void populateUi() {
        viewModel.getObservableArticles().observe(
                getViewLifecycleOwner(),
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        // TODO Postpone enter transition
                        postponeEnterTransition();
                        Timber.d("Observe article list.");
                        if (resource != null && resource.data != null) {
                            adapter.submitList(resource.data);
                        }
                    }
                });
    }

    /**
     * Scrolls the recycler view to show the last viewed item in the grid.
     * This is important when navigating back from the grid.
     *
     * See: https://github.com/android/animation-samples/tree/master/GridToPager
     */
    private void scrollToPosition() {
        binding.articlesRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top,  int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight,  int oldBottom) {
                binding.articlesRecyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager =
                        binding.articlesRecyclerView.getLayoutManager();
                View viewAtPosition = Objects.requireNonNull(layoutManager)
                        .findViewByPosition(viewModel.getCurrentPosition());
                // Scroll to position if the view for the current position is null (not
                // currently part of layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(
                        viewAtPosition, false, true)) {
                    binding.articlesRecyclerView.post(() ->
                            layoutManager.scrollToPosition(viewModel.getCurrentPosition()));
                }
            }
        });
    }

}
