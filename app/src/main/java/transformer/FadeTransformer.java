package transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class FadeTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        final float normalizedposition = Math.abs(Math.abs(position) - 1);
        page.setAlpha(normalizedposition);

    }
}
