package com.ang.acb.materialme.ui.details;

import android.content.Context;
import android.os.Bundle;

import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticlesViewPagerBinding;
import com.ang.acb.materialme.ui.viewmodel.ArticlesViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


/**
 * A fragment for displaying a pager of articles.
 *
 * See: https://github.com/android/animation-samples/tree/master/GridToPager
 * See: https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html
 */
public class ArticlesPagerFragment extends Fragment {

    private FragmentArticlesViewPagerBinding binding;
    private ArticlesViewModel viewModel;
    private ViewPagerAdapter viewPagerAdapter;
    private int position;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public ArticlesPagerFragment() {}

    @Override
    public void onAttach(@NotNull Context context) {
        // Note: when using Dagger for injecting Fragment objects, inject as early as possible.
        // For this reason, call AndroidInjection.inject() in onAttach(). This also prevents
        // inconsistencies if the Fragment is reattached.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentArticlesViewPagerBinding.inflate(inflater, container, false);

        initViewModel();
        setupViewPagerAdapter();
        populateUi();
        updateCurrentPagePosition();

        prepareSharedElementTransition();

        // Avoid a postponeEnterTransition() on orientation change,
        // and postpone only of first creation.
        if (savedInstanceState == null) postponeEnterTransition();

        return binding.getRoot();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticlesViewModel.class);
        viewModel.setCurrentPosition(position);
        Timber.d("Set current position: %s in articles view model.", position);
    }

    private void setupViewPagerAdapter(){
        // Initialize ViewPagerAdapter with the child fragment manager.
        viewPagerAdapter = new ViewPagerAdapter(
                getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.articlesViewPager.setAdapter(viewPagerAdapter);
    }

    private void populateUi() {
        viewModel.getObservableArticles().observe(
                getViewLifecycleOwner(),
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        if (resource != null && resource.data != null) {
                            viewPagerAdapter.submitList(resource.data);
                            binding.articlesViewPager.setCurrentItem(viewModel.getCurrentPosition());
                        }
                    }
        });
    }

    private void updateCurrentPagePosition(){
        // Save current page position in articles view model.
        binding.articlesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                viewModel.setCurrentPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /**
     * Prepares the shared element transition from and back to the articles grid fragment.
     */
    private void prepareSharedElementTransition() {
        setSharedElementEnterTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.image_shared_element_transition));

        // Note: a similar mapping is set in the ArticlesGridFragment
        // with a setExitSharedElementCallback().
        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the image view at the primary fragment (the ArticleDetailsFragment
                        // that is currently visible). To locate the fragment, call instantiateItem
                        // with the selection position. At this stage, the method will simply return
                        // the fragment at the position and will not create a new one.
                        Fragment currentFragment = (Fragment) viewPagerAdapter.instantiateItem(
                                binding.articlesViewPager, viewModel.getCurrentPosition());

                        View view = currentFragment.getView();
                        if (view == null) return;

                        // Map the first shared element name to the child ImageView.
                        sharedElements.put(
                                names.get(0),
                                view.findViewById(R.id.details_article_photo));
                    }
                });
    }

}
