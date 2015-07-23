package hn.jepz.www.yugiohlifecounter;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by jepz2_000 on 4/25/2015.
 */
public class Flip3dAnimation extends Animation {
    private final float mFromDegrees, mToDegrees, mCenterX,mCenterY;
    private Camera mCamera;
    private int mDirection;

    public Flip3dAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, int direction) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDirection = direction;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + (( mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;

        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();
        //Si es 1 es vertical
        if (mDirection == 1) {
            camera.rotateX(degrees);
        //Cualquier otra cosa es horizontal
        } else  {
            camera.rotateY(degrees);
        }

        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX,centerY);
        super.applyTransformation(interpolatedTime, t);
    }
}
