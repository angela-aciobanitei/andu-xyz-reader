package com.ang.acb.materialme.ui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

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

import org.jetbrains.annotations.NotNull;

import java.util.Date;
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
        Timber.d("Setup articles view model.");
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
            Article article = resource.data.get(position);
            binding.setArticle(article);

            binding.contentPartialDetails.articleTitle.setText(article.getTitle());
            binding.contentPartialDetails.articleByline.setText(Utils.formatArticleByline(
                    Utils.formatPublishedDate(article.getPublishedDate()),
                    article.getAuthor()));

            binding.contentPartialDetails.articleBody.setText(Html.fromHtml(article.getBody()
                    .substring(0, 2500)
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
                    .dontAnimate()
                    .placeholder(R.color.photoPlaceholder)
                    // This listener tells when the image is done loading
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object
                                model, Target<Bitmap> target, boolean isFirstResource) {
                            // TODO scheduleStartPostponedTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            generatePalette(resource);
                            // TODO scheduleStartPostponedTransition();
                            return false;
                        }
                    })
                    .into(binding.detailsArticlePhoto);

            binding.executePendingBindings();
        }
    }

    private void generatePalette(Bitmap resource) {
        // Generate palette synchronously.
        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = Utils.getDominantColor(palette);
                if (swatch != null) {
                    binding.contentPartialDetails.metaBar.setBackgroundColor(swatch.getRgb());
                    binding.detailsCollapsingToolbar.setContentScrimColor(swatch.getRgb());
                }
            }
        });
    }

    /**
     * Helper method that calls startPostponedEnterTransition().
     * Note: postponeEnterTransition() is called on the parent, ArticlesPagerFragment, so
     * startPostponedEnterTransition() should also be called on it to get the transition
     * going. But before calling startPostponedEnterTransition(), we first need to make
     * sure that the image view is drawn.
     * See: https://medium.com/@ayushkhare/shared-element-transitions-4a645a30c848
     */
    private void scheduleStartPostponedTransition(final ImageView imageView) {
        // To make sure our shared view is drawn, we use the ViewTreeObserver class.
        imageView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                        getParentFragment().startPostponedEnterTransition();
                        return true;
                    }
                });
    }
}
