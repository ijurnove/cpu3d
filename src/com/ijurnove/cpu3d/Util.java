package com.ijurnove.cpu3d;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

class Util {
    protected static double clamp(double val, double min, double max) {
        return Math.min(Math.max(val, min), max);
    }

    protected static double baryInterpolate(double[] bary, double val0, double val1, double val2) {
        return
        (bary[0] * val0) +
        (bary[1] * val1) +
        (bary[2] * val2);
    }

    protected static double distance2D(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    protected static double[] sphereToCartesian(double radius, double theta, double phi) {
        return new double[] {
            (radius * sin(phi) * sin(theta)),
            (radius * sin(phi) * cos(theta)),
            (radius * cos(phi))
        };
    }

    // protected static double[] cartesianToSphere(double x, double y, double z) {
    //     double radius = Math.sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));
    //     double theta = Math.acos(z/radius);
    //     double phi = Math.signum(y) * Math.acos(x/Math.sqrt(pow(x, 2) + pow(y,2)));

    //     return new double[] {radius, theta, phi};
    // }
}
