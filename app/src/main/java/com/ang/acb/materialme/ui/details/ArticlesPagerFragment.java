package com.ang.acb.materialme.ui.details;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.Transition;
import androidx.viewpager.widget.ViewPager;
import androidx.transition.TransitionInflater;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticlesViewPagerBinding;
import com.ang.acb.materialme.ui.viewmodel.ArticlesViewModel;
import com.ang.acb.materialme.ui.grid.ArticleGridFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


/**
 * A fragment for displaying a view pager of articles.
 *
 * See: https://github.com/android/animation-samples/tree/master/GridToPager
 * See: https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html
 */
public class ArticlesPagerFragment extends Fragment {

    private FragmentArticlesViewPagerBinding binding;
    private ArticlesViewModel viewModel;
    private ViewPagerAdapter viewPagerAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public ArticlesPagerFragment() {}

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Avoid a postponeEnterTransition() on orientation change,
        // and postpone only on first creation.
        if (savedInstanceState == null) postponeEnterTransition();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentArticlesViewPagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        setupViewPagerAdapter();
        updateCurrentPagePosition();
        populateUi();
        prepareTransitions();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticlesViewModel.class);
    }

    private void setupViewPagerAdapter(){
        // Because ArticlesPagerFragment contains a series of article details fragments
        // we need to initialize the view pager adapter with the child fragment manager.
        viewPagerAdapter = new ViewPagerAdapter(
                getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.articlesViewPager.setAdapter(viewPagerAdapter);
    }

    private void updateCurrentPagePosition(){
        // Add a listener that will update the selection coordinator when paging
        // the articles and save the current page position in articles view model.
        binding.articlesViewPager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        viewModel.setCurrentPosition(position);
                    }
                });
    }

    private void populateUi() {
        viewModel.getObservableArticles().observe(
                getViewLifecycleOwner(),
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        if (resource != null && resource.data != null) {
                            // Update data in the view pager adapter.
                            viewPagerAdapter.submitList(resource.data);

                            // Set the currently selected page for the view pager.
                            // To transition immediately, set smoothScroll to false.
                            binding.articlesViewPager.setCurrentItem(
                                    viewModel.getCurrentPosition(), false);
                        }
                    }
                }
        );
    }

    /**
     * Prepares the shared element transition from and back to the {@link ArticleGridFragment}.
     */
    private void prepareTransitions() {
        Transition enterTransition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.image_view_enter_transition);
        enterTransition.setDuration(375);
        setSharedElementEnterTransition(enterTransition);

        // We would like to support a seamless back and forth transition. This includes
        // a transition from the grid to the pager, and then a transition back to the
        // relevant image, even when the user paged to a different image. To do so, we
        // will need to find a way to dynamically remap the shared elements. To do this,
        // first, we'll set a transition name on the image views by calling setTransitionName.
        // Then  we'll set up SharedElementCallbacks to intercept onMapSharedElements() and
        // adjust the mapping of the shared element names to views. This will be done when
        // exiting the ArticleGridFragment and when entering the ArticlesPagerFragment.
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // Locate the image view at the primary fragment (the ArticleDetailsFragment
                // that is currently visible). To locate the fragment, call instantiateItem()
                // with the current position. At this stage, the method will simply return
                // the fragment at the position and will not create a new one.
                Fragment currentFragment = (Fragment) viewPagerAdapter.instantiateItem(
                        binding.articlesViewPager, viewModel.getCurrentPosition());

                // Get the root view for the current fragment layout.
                View rootView = currentFragment.getView();
                if (rootView == null) return;

                // We are only interested in a single ImageView transition from the grid to the
                // fragment the view-pager holds, so the mapping only needs to be adjusted for
                // the first named element received at the onMapSharedElements() callback.
                ImageView transitioningView = rootView.findViewById(R.id.details_article_photo);
                sharedElements.put(names.get(0), transitioningView);
            }
        });
    }
}
