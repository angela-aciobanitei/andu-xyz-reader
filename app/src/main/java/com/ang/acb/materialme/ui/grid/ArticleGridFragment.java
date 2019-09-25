package com.ang.acb.materialme.ui.grid;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

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
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticleGridBinding;
import com.ang.acb.materialme.ui.viewmodel.ArticlesViewModel;
import com.ang.acb.materialme.ui.common.MainActivity;
import com.ang.acb.materialme.ui.details.ArticlesPagerFragment;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        initViewModel();
        setupRecyclerView();
        prepareTransitions();
        initViewHolderListener();
        setupAdapter();
        populateUi();
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
     * Prepares the shared element transition to and back to the {@link ArticlesPagerFragment}.
     */
    private void prepareTransitions() {
        isEnterTransitionStarted = new AtomicBoolean();
        // To make our transitions even smoother, we would like to fade out
        // the grid items when the image transitions to the pager view.
        Transition gridExitTransition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition) ;
        gridExitTransition.setDuration(325);
        setExitTransition(gridExitTransition);

        // We would like to support a seamless back and forth transition. This includes
        // a transition from the grid to the pager, and then a transition back to the
        // relevant image, even when the user paged to a different image. To do so, we
        // will need to find a way to dynamically remap the shared elements. To do this,
        // first, we'll set a transition name on the image views by calling setTransitionName.
        // Then  we'll set up SharedElementCallbacks to intercept onMapSharedElements() and
        // adjust the mapping of the shared element names to views. This will be done when
        // exiting the ArticleGridFragment and when entering the ArticlesPagerFragment.
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // Locate the ViewHolder for the clicked position.
                RecyclerView.ViewHolder selectedViewHolder = binding.articlesRecyclerView
                        .findViewHolderForAdapterPosition(viewModel.getCurrentPosition());
                if (selectedViewHolder == null || selectedViewHolder.itemView == null) return;

                // We are only interested in a single ImageView transition from the grid to the
                // fragment the view-pager holds, so the mapping only needs to be adjusted for
                // the first named element received at the onMapSharedElements() callback.
                ImageView transitioningView = selectedViewHolder.itemView
                        .findViewById(R.id.article_item_thumbnail);
                sharedElements.put(names.get(0),transitioningView);
            }
        });
    }

    private void initViewHolderListener() {
        listener = new ViewHolderListener() {
            @Override
            public void onItemClicked(View view, int position) {
                // Save current position to view model.
                viewModel.setCurrentPosition(position);

                // Exclude the clicked card from the exit transition (e.g. the card will
                // disappear immediately instead of fading out with the rest to prevent
                // an overlapping animation of fade and move).
                ((TransitionSet) getExitTransition()).excludeTarget(view, true);

                // Create the shared element transition extras.
                ImageView transitioningView = view.findViewById(R.id.article_item_thumbnail);
                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(transitioningView, transitioningView.getTransitionName())
                        .build();

                // Navigate to destination (ArticlesPagerFragment),
                // passing in the shared element as extras.
                NavHostFragment.findNavController(ArticleGridFragment.this)
                        .navigate(R.id.action_article_grid_to_articles_view_pager,
                                null, null, extras);
            }

            @Override
            public void onLoadCompleted(int position) {
                // Call startPostponedEnterTransition() only when the
                // selected image loading is completed.
                if (viewModel.getCurrentPosition() != position) return;
                if (isEnterTransitionStarted.getAndSet(true)) return;
                schedulePostponedEnterTransition();
            }
        };
    }

    private void schedulePostponedEnterTransition() {
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
                }
        );
    }

    /**
     * Scrolls the recycler view to show the last viewed item in the grid.
     * This is important when navigating back from the grid.
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
