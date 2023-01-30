package io.github.darealturtywurty.turtylib.core.util;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.darealturtywurty.turtylib.client.util.FourVec2;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;

public final class MathUtils {
    public static boolean isWithinArea(int xPos, int yPos, int x, int y, int width, int height) {
        return xPos >= x && yPos >= y && xPos < x + width && yPos < y + height;
    }

    public static FourVec2 getFourVec(PoseStack stack, float x1, float y1, float x2, float y2, float width) {
        final var start = new Vec2(x1, y1);
        final var end = new Vec2(x2, y2);

        final var vector = end.add(start.negated()).normalized();
        final var side = new Vec2(-vector.y, vector.x); // vector.perpendicular()

        final var pt1 = start.add(side.scale(width).negated());
        final var pt2 = end.add(side.scale(width).negated());
        final var pt3 = end.add(side.scale(width));
        final var pt4 = start.add(side.scale(width));

        return new FourVec2(pt1, pt2, pt3, pt4);
    }

    public static double mapNumber(double value, double rangeMin, double rangeMax, double resultMin, double resultMax) {
        return (value - rangeMin) / (rangeMax - rangeMin) * (resultMax - resultMin) + resultMin;
    }

    public static int mapToInt(double value, double rangeMin, double rangeMax, double resultMin, double resultMax) {
        return (int) mapNumber(value, rangeMin, rangeMax, resultMin, resultMax);
    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        final int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c", count / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public static float distanceBetweenPoints3D(final BlockPos firstPosition, final BlockPos secondPosition) {
        final float x1 = firstPosition.getX();
        final float y1 = firstPosition.getY();
        final float z1 = firstPosition.getZ();
        final float x2 = secondPosition.getX();
        final float y2 = secondPosition.getY();
        final float z2 = secondPosition.getZ();
        final float differenceX = x2 - x1;
        final float differenceY = y2 - y1;
        final float differenceZ = z2 - z1;
        return Mth.sqrt((differenceX * differenceX) + (differenceY * differenceY) + (differenceZ * differenceZ));
    }

    public static float distanceBetweenPoints2D(final BlockPos firstPosition, final BlockPos secondPosition) {
        final float x1 = firstPosition.getX();
        final float y1 = firstPosition.getY();
        final float x2 = secondPosition.getX();
        final float y2 = secondPosition.getY();
        final float differenceX = x2 - x1;
        final float differenceY = y2 - y1;
        return Mth.sqrt((differenceX * differenceX) + (differenceY * differenceY));
    }

    public static float distanceBetweenPoints2D(final float firstPointX, final float firstPointY, final float secondPointX, final float secondPointY) {
        final float differenceX = secondPointX - firstPointX;
        final float differenceY = secondPointY - firstPointY;
        return Mth.sqrt((differenceX * differenceX) + (differenceY * differenceY));
    }

    public static float nextFloat(RandomSource pRandom, float pMin, float pMax) {
        return pMin + pRandom.nextFloat() * (pMax - pMin);
    }
}
