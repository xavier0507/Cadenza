package xy.hippocampus.cadenza.controller.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import de.hdodenhof.circleimageview.CircleImageView;
import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.fragment.base.BaseFragment;
import xy.hippocampus.cadenza.controller.manager.FragmentStackManager;

import static xy.hippocampus.cadenza.model.constant.Constants.FRAG_ABOUT_TAG;

/**
 * Created by Xavier Yin on 2017/7/26.
 */

public class AboutFragment extends BaseFragment implements View.OnClickListener {
    private ViewGroup aboutRootLayout;
    private Toolbar toolbar;
    private ImageView backImageView;

    private ImageView cadenzaHeaderImage;
    private ImageView cadenzaPhotoCircleImage;
    private TextView cadenzaVersionText;

    private ImageView xavierHeaderImage;
    private CircleImageView xavierPhotoCircleImage;
    private TextView xavierSendText;

    private ImageView jimHeaderImage;
    private CircleImageView jimPhotoCircleImage;
    private TextView jimSendText;

    @Override
    protected int layoutResourceId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void findView(View root) {
        super.findView(root);

        this.aboutRootLayout = (ViewGroup) root.findViewById(R.id.layout_about_root);
        this.toolbar = (Toolbar) root.findViewById(R.id.main_toolbar);
        this.backImageView = (ImageView) root.findViewById(R.id.main_imageview_back);

        this.cadenzaHeaderImage = (ImageView) root.findViewById(R.id.img_about_cadenza_header);
        this.cadenzaPhotoCircleImage = (ImageView) root.findViewById(R.id.img_about_cadenza_photo);
        this.cadenzaVersionText = (TextView) root.findViewById(R.id.text_about_cadenza_version);

        this.xavierHeaderImage = (ImageView) root.findViewById(R.id.img_about_xavier_header);
        this.xavierPhotoCircleImage = (CircleImageView) root.findViewById(R.id.img_about_xavier_photo);
        this.xavierSendText = (TextView) root.findViewById(R.id.text_about_xavier_email);

        this.jimHeaderImage = (ImageView) root.findViewById(R.id.img_about_jim_header);
        this.jimPhotoCircleImage = (CircleImageView) root.findViewById(R.id.img_about_jim_photo);
        this.jimSendText = (TextView) root.findViewById(R.id.text_about_jim_email);
    }

    @Override
    protected void assignViewSettings(View root) {
        super.assignViewSettings(root);

        this.updateColor();
        this.loadInfo();
        this.loadImage();
    }

    @Override
    protected void addEvents(View root) {
        super.addEvents(root);

        this.backImageView.setOnClickListener(this);
        this.xavierSendText.setOnClickListener(this);
        this.jimSendText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_imageview_back:
                if (this != null && FragmentStackManager.getInstance().includesKey(FRAG_ABOUT_TAG)) {
                    this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                    FragmentStackManager.getInstance().popFragment(FRAG_ABOUT_TAG);
                }
                break;

            case R.id.text_about_xavier_email:
                this.sendMail(this.getString(R.string.app_about_xavier_email));
                break;

            case R.id.text_about_jim_email:
                this.sendMail(this.getString(R.string.app_about_jim_email));
                break;
        }
    }

    private void loadImage() {
        Glide.with(this)
                .load(R.drawable.bg_about_cadenza_header)
                .priority(Priority.HIGH)
                .animate(android.R.anim.fade_in)
                .into(this.cadenzaHeaderImage);
        Glide.with(this)
                .load(R.drawable.ic_clef_note)
                .priority(Priority.HIGH)
                .animate(android.R.anim.fade_in)
                .into(this.cadenzaPhotoCircleImage);

        Glide.with(this)
                .load(R.drawable.bg_about_xavier_header)
                .priority(Priority.HIGH)
                .animate(android.R.anim.fade_in)
                .into(this.xavierHeaderImage);
        Glide.with(this)
                .load(R.drawable.bg_about_xavier_photo)
                .priority(Priority.HIGH)
                .animate(android.R.anim.fade_in)
                .into(this.xavierPhotoCircleImage);

        Glide.with(this)
                .load(R.drawable.bg_about_jim_header)
                .priority(Priority.HIGH)
                .animate(android.R.anim.fade_in)
                .into(this.jimHeaderImage);
        Glide.with(this)
                .load(R.drawable.bg_about_jim_photo)
                .priority(Priority.HIGH)
                .animate(android.R.anim.fade_in)
                .into(this.jimPhotoCircleImage);
    }

    private void updateColor() {
        this.aboutRootLayout.setBackgroundColor(this.primaryColor);
        this.toolbar.setBackgroundColor(this.primaryColor);

        GradientDrawable bgDrawable = (GradientDrawable) this.cadenzaPhotoCircleImage.getBackground();
        bgDrawable.setColor(this.primaryColor);
    }

    private void loadInfo() {
        if (this.isDebugVersion()) {
            StringBuffer version = new StringBuffer();
            version.append(this.getCurrentVersion()).append("_");

            StringBuffer flavor = new StringBuffer();
            flavor.append(this.getCurrentFlavor()).append("_").append(this.getCurrentBuildType());

            this.cadenzaVersionText.setText(this.getString(R.string.app_version, version, flavor));
        } else {
            this.cadenzaVersionText.setText(this.getString(R.string.app_version, this.getCurrentVersion(), ""));
        }
    }

    private void sendMail(String mail) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(("mailto:" + mail)));

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), this.getString(R.string.exception_email_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }
}
