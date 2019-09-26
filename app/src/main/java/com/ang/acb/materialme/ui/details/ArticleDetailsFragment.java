package com.ang.acb.materialme.ui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticleDetailsBinding;
import com.ang.acb.materialme.ui.viewmodel.ArticlesViewModel;
import com.ang.acb.materialme.ui.common.MainActivity;
import com.ang.acb.materialme.utils.GlideApp;
import com.ang.acb.materialme.utils.Utils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

public class ArticleDetailsFragment extends Fragment {

    private static final String ARG_ARTICLE_ID = "ARG_ARTICLE_ID";
    public static final String ARG_POSITION = "ARG_POSITION";

    private FragmentArticleDetailsBinding binding;
    private ArticlesViewModel viewModel;
    private int position;
    private long articleId;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    // Required empty public constructor
    public ArticleDetailsFragment() {}

    public static ArticleDetailsFragment newInstance(int position, long articleId) {
        ArticleDetailsFragment fragment = new ArticleDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putLong(ARG_ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            articleId = getArguments().getLong(ARG_ARTICLE_ID);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentArticleDetailsBinding.inflate(inflater, container, false);

        // TODO Set the string value of the article id as the unique transition name for the view.
        ViewCompat.setTransitionName(binding.detailsArticlePhoto, String.valueOf(articleId));

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

    private MainActivity getHostActivity() {
        return (MainActivity)getActivity();
    }

    private void setupToolbar() {
        getHostActivity().setSupportActionBar(binding.detailsToolbar);

        binding.detailsToolbar.setNavigationOnClickListener(view ->
                // Attempts to navigate up in the navigation hierarchy.
                NavHostFragment.findNavController(ArticleDetailsFragment.this)
                        .navigateUp());
        if (getHostActivity().getSupportActionBar() != null) {
            // Handle Up navigation and hide title
            getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getHostActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setToolbarTitleIfCollapsed(Article article) {
        // To set the toolbar title only when the toolbar is collapsed, we need to add an
        // OnOffsetChangedListener on AppBarLayout to determine when CollapsingToolbarLayout
        // is collapsed or expanded. This listener is triggered when vertical offset is changed,
        // i.e bottom and top are offset.
        // See: https://medium.com/@nullthemall/the-power-of-appbarlayout-offset-ecbf8eaa6b5f
        binding.detailsAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShown = true;
            int totalScrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // The verticalOffset parameter is always between 0 and
                // appBarLayout.getTotalScrollRange(), which is the total
                // height of all the children that can scroll.
                if (totalScrollRange == -1) {
                    totalScrollRange = appBarLayout.getTotalScrollRange();
                }
                // If toolbar is completely collapsed, set the collapsing bar title.
                if (totalScrollRange + verticalOffset == 0) {
                    binding.detailsCollapsingToolbar.setTitle(article.getTitle());
                    isShown = true;
                } else if (isShown) {
                    // When toolbar is expanded, display an empty string.
                    binding.detailsCollapsingToolbar.setTitle(" ");
                    isShown = false;
                }
            }
        });
    }

    private void setupShareFab() {
        binding.contentPartialDetails.shareFab.setOnClickListener(view ->
                startActivity(Intent.createChooser(
                        ShareCompat.IntentBuilder.from(getHostActivity())
                                .setType("text/plain")
                                .setText("Some sample text")
                                .getIntent(), getString(R.string.action_share))));
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticlesViewModel.class);
        viewModel.setCurrentPosition(position);
    }

    private void observeArticleDetails() {
        viewModel.getObservableArticles().observe(
                getViewLifecycleOwner(),
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        Timber.d("Observe article item.");
                        populateUi(resource);
                    }
        });
    }

    private void populateUi(Resource<List<Article>> resource){
        if (resource != null && resource.data != null) {
            Article article = resource.data.get(viewModel.getCurrentPosition());
            //if (getHostActivity().getSupportActionBar() != null) setToolbarTitleIfCollapsed(article);

            binding.contentPartialDetails.articleTitle.setText(article.getTitle());
            binding.contentPartialDetails.articleByline.setText(Utils.formatArticleByline(
                    Utils.formatPublishedDate(article.getPublishedDate()),
                    article.getAuthor()));

            binding.contentPartialDetails.articleBody.setText(Html.fromHtml(article.getBody()
                    .substring(0, 1000)
                    .replaceAll("\r\n\r\n", "<br /><br />")
                    .replaceAll("\r\n", " ")
                    .replaceAll(" {2}", "")));

            binding.contentPartialDetails.readMoreButton.setOnClickListener(view -> {
                binding.contentPartialDetails.readMoreButton.setVisibility(View.GONE);
                binding.contentPartialDetails.articleBody.setText(Html.fromHtml(article.getBody()
                        .replaceAll("\r\n\r\n", "<br /><br />")
                        .replaceAll("\r\n", " ")
                        .replaceAll(" {2}", "")));

            });

            GlideApp.with(this)
                    .asBitmap()
                    .load(article.getPhotoUrl())
                    // Tell Glide not to use its standard crossfade animation.
                    .dontAnimate()
                    // Display a placeholder until the image is loaded and processed.
                    .placeholder(R.color.photoPlaceholder)
                    // Keep track of errors and successful image loading.
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object
                                model, Target<Bitmap> target, boolean isFirstResource) {
                            schedulePostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            generatePaletteAsynchronously(resource);
                            schedulePostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(binding.detailsArticlePhoto);

            // Binding needs to be executed immediately.
            binding.executePendingBindings();
        }
    }

    private void schedulePostponedEnterTransition() {
        // Before calling startPostponedEnterTransition(), make sure that the
        // view is drawn first using ViewTreeObserver's OnPreDrawListener.
        // https://medium.com/@ayushkhare/shared-element-transitions-4a645a30c848.
        binding.getRoot().getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        binding.getRoot().getViewTreeObserver().removeOnPreDrawListener(this);
                        // The postponeEnterTransition() is called on the parent, the
                        // ArticlesPagerFragment, so the startPostponedEnterTransition()
                        // should also be called on the parent to get the transition going.
                        getParentFragment().startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    private void generatePaletteAsynchronously(Bitmap bitmap) {
        // By passing in a PaletteAsyncListener to the generate() method, we can generate
        // the palette asynchronously using an AsyncTask to gather the Palette swatch
        // information from the bitmap. When a palette is generated, a number of colors
        // with different profiles are extracted from the image: vibrant, vibrant dark,
        // vibrant light, muted, muted dark, muted light.
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = Utils.getDominantColor(palette);
                if (swatch != null) {
                    binding.contentPartialDetails.metaBar.setBackgroundColor(swatch.getRgb());
                    binding.detailsCollapsingToolbar.setContentScrimColor(swatch.getRgb());
                }
            }
        });
    }
}
