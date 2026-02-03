package com.ijurnove.cpu3d;
import static java.lang.Math.pow;

class LightCalc {
    protected static double[] phongLighting(Camera cam, Point3d point, Vector3d normalVec, Scene scene, Material mat, Shape3d parentObject) {
        if (parentObject.getShapeFlag(ShapeFlag.RECIEVE_LIGHTING) == 1 && scene.getFlag(SceneFlag.DO_LIGHTING) == 1) {
            Vector3d viewVec = new Vector3d(cam.getPos(), point);
            viewVec.normalize();
            normalVec.normalize();
            
            double[] intensities = new double[3];
            for (Light light : scene.getLights()) {
                Vector3d lightVec = light.lightVec(point);
                lightVec.swapXY();

                // double decay = light.distance(lightVec);
                double decay = light.distance(lightVec);
                if (light.getType() == LightType.POINT) {
                    decay = Math.max(1, Math.pow(decay, ((PointLight) light).getDecay()));
                }

                lightVec.normalize();

                double shadow = 1;
                if (scene.getFlag(SceneFlag.DO_SHADOWS) == 1) {
                    shadow = light.shadowValue(point);
                }
                
                
                double[] emis = mat.getEmissive();
                double[] amb = phongAmbient(mat, light);
                double[] diff = phongDiffuse(mat, light, normalVec, lightVec);
                double[] spec = blinnPhongSpecular(mat, light, lightVec, viewVec, normalVec);

                for (int i = 0; i < 3; i++) {
                    intensities[i] += emis[i] + amb[i] + 
                        ((diff[i] / decay) + 
                         (spec[i] / decay))
                        * shadow;
                }
            }

            return intensities;
        } else {
            return new double[] {1, 1, 1};
        }
    }

    private static double[] phongAmbient(Material mat, Light light) {
        double[] returnVal = new double[3];

        for (int i = 0; i < 3; i++) {
            returnVal[i] = light.getComponents().getAmb()[i] * mat.getPhongComponents().getAmb()[i];
        }

        return returnVal;
    }

    private static double[] phongDiffuse(Material mat, Light light, Vector3d normVec, Vector3d lightVec) {
        double[] returnVal = new double[3];
        
        double multiplier = Math.max(Vector3d.dotProduct(normVec, lightVec), 0);
        
        for (int i = 0; i < 3; i++) {
            returnVal[i] = mat.getPhongComponents().getDiff()[i] * multiplier * light.getComponents().getDiff()[i];
        }

        return returnVal;
    }

    private static double[] blinnPhongSpecular(Material mat, Light light, Vector3d lightVec, Vector3d viewVec, Vector3d normalVec) {
        double[] returnVal = new double[3];

        Vector3d halfwayVec = Vector3d.add(lightVec, viewVec);
        halfwayVec.normalize();
        double multiplier = pow(Math.max(Vector3d.dotProduct(normalVec, halfwayVec), 0), mat.getShininess());

        for (int i = 0; i < 3; i++) {
            returnVal[i] = mat.getPhongComponents().getSpec()[i] * multiplier * light.getComponents().getSpec()[i];
        }

        return returnVal;
    }
}
