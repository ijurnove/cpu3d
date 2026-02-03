package com.ijurnove.cpu3d;

/**
 * A <code>Matrix</code> stores a two dimensional array of doubles.
 */
public class Matrix {
    private final double[][] values;
    private final int columns;
    private final int rows;

    /**
     * Constructs a new <code>Matrix</code> filled with a specified set of values.
     * @param values the initial Matrix values
     */
    public Matrix(double[][] values) {
        this.values = values;

        this.rows = values.length;
        this.columns = values[0].length;
    }

    /**
     * Returns the values within the <code>Matrix</code> as a <code>double[][]</code>.
     * @return the values within this Matrix
     */
    public double[][] getValues() { return this.values; }

    /**
     * Returns the number of columns.
     * @return the number of columns
     */
    public int columnCount() { return this.columns; }

    /**
     * Returns the number of rows.
     * @return the number of rows
     */
    public int rowCount() { return this.rows; }
    
    /**
     * Sets the item at a specified row and column.
     * @param col the column of the item to set
     * @param row the row of the item to set
     * @param newVal the value to set
     */
    public void setValue(int col, int row, double newVal) {
        this.values[row][col] = newVal;
    }

    /**
     * Returns the value at a specified row and column.
     * @param col the column of the item
     * @param row the row of the item
     */
    public double getValue(int col, int row) {
        return this.values[row][col];
    }

    /**
     * Returns an entire row of the <code>Matrix</code> as a <code>double[]</code>
     * @param rowNum the row to be returned
     * @return a specified row
     */
    public double[] getRow(int rowNum) {
        return this.values[rowNum];
    }
    
    /**
     * Returns an entire column of the <code>Matrix</code> as a <code>double[]</code>.
     * @param columnNum the column to be returned
     * @return a specified column
     */
    public double[] getColumn(int columnNum) {
        double[] column = new double[this.rows];

        for (int i = 0; i < this.rows; i++) {
            column[i] = this.values[i][columnNum];
        }

        return column;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (double[] row : this.values) {
            builder.append("[ ");
            for (double item : row) {
                builder.append(item);
                builder.append(", ");
            }
            builder.deleteCharAt(builder.length() - 2);
            builder.append("]\n");
        }

        return builder.toString();
    }

    /**
     * Sets all values in this <code>Matrix</code> to the values of another.
     * @param newValues the Matrix to fill this one
     */
    public void setValues(Matrix newValues) {
        if (newValues.rows != this.rows || newValues.columns != this.columns) {
            throw new ArrayIndexOutOfBoundsException("Arrays are of different sizes in Matrix.setValues");
        }

        for (int row = 0; row < newValues.rows; row++) {
            for (int column = 0; column < newValues.columns; column++) {
                this.setValue(column, row, newValues.getValue(column, row));
            }
        }
    }

    /**
     * Multiplies all items of a <code>Matrix</code> by a number.
     * @param m the Matrix to be multiplied
     * @param factor the number to multiply all numbers in the Matrix by
     */
    public static void scalarMult(Matrix m, double factor) {
        for (int row = 0; row < m.rows; row++) {
            for (int column = 0; column < m.columns; column++) {
                m.values[row][column] *= factor;
            }
        }
    }

    /**
     * Multiplies all items of this <code>Matrix</code> by a number.
     * @param factor the number to multiply all numbers in this Matrix by
     */
    public void scalarMult(double factor) {
        for (int row = 0; row < this.rows; row++) {
            for (int column = 0; column < this.columns; column++) {
                this.values[row][column] *= factor;
            }
        }
    }

    /**
     * Multiplies two <code>Matrix</code>es together.
     * @param matrix1 the first Matrix
     * @param matrix2 the second Matrix
     * @return the product of the two Matrices
     */
    public static Matrix multiply(Matrix matrix1, Matrix matrix2) {
        if (matrix1.columns != matrix2.rows) {
            throw new ArithmeticException("Matrices cannot be multiplied: columns of matrix1 are not equal to rows in matrix2");
        }

        double[][] product = new double[matrix1.rows][matrix2.columns];

        for (int row = 0; row < product.length; row++) {
            for (int column = 0; column < product[0].length; column++) {

                double[] mult = matrix2.getColumn(column);
                for (int i = 0; i < mult.length; i++) {
                    product[row][column] += mult[i] * matrix1.values[row][i];
                }

            }
        }

        return new Matrix(product);
    }

    /**
     * Multiplies this <code>Matrix</code> by another.
     * @param matrix2 the Matrix to multiply this one by
     * @return the product of this Matrix and the given Matrix
     */
    public Matrix multiply(Matrix matrix2) {
        if (this.columns != matrix2.rows) {
            throw new ArithmeticException("Matrices cannot be multiplied: columns of matrix1 are not equal to rows in matrix2");
        }

        double[][] product = new double[this.rows][matrix2.columns];

        for (int row = 0; row < product.length; row++) {
            for (int column = 0; column < product[0].length; column++) {

                double[] mult = matrix2.getColumn(column);
                for (int i = 0; i < mult.length; i++) {
                    product[row][column] += mult[i] * this.values[row][i];
                }

            }
        }

        return new Matrix(product);
    }

    // updates renderCoords to scale this to depth - call 2nd
    private void scaleToDepth() {
        this.setValue(0, 2, getValue(0, 2) * -1);
        double z = this.getValue(0, 2);
        if (z == 0) {
            z = 1;
        }
        this.setValue(0, 0, this.getValue(0, 0) / z);
        this.setValue(0, 1, this.getValue(0, 1) / z);
    }

    // scales coordinates with fov - call 3rd
    private void scaleFOV(double fov) {
        this.setValue(0, 0, this.getValue(0, 0) / Math.tan(Math.toRadians(fov) / 2));
        this.setValue(0, 1, this.getValue(0, 1) / Math.tan(Math.toRadians(fov) / 2));
    }

    // sets coordinates to screen positions - call 4th
    private void scaleToScreen(Scene scene) {
        this.setValue(0, 0, (this.getValue(0, 0) * scene.getPixelWidth()) + (scene.getPixelWidth() / 2));
        this.setValue(0, 1, (this.getValue(0, 1) * scene.getPixelHeight() * (-1 - (scene.getPixelHeight() / scene.getPixelHeight()))) + (scene.getPixelHeight() / 2));
    }

    protected void perspRenderUpd(Camera c) {
        this.scaleToDepth();
        this.scaleFOV(c.getFov());
        this.scaleToScreen(c.getParent());
    }

    protected void perspRenderUpdForPointShadowMap(Camera c) {
        this.scaleToDepth();
        this.scaleFOV(c.getFov());
        this.scaleToBounds((int) c.getParent().getInitFlag(SceneInitFlag.SHADOW_RESOLUTION_ACROSS), (int) c.getParent().getInitFlag(SceneInitFlag.SHADOW_RESOLUTION_UP));
    }

    private void scaleToBounds(int width, int height) {
        this.setValue(0, 0, (this.getValue(0, 0) * width) + (width / 2));
        this.setValue(0, 1, (this.getValue(0, 1) * height) + (height / 2));   
    }

    protected void orthoRenderUpd(Camera c) {
        this.scaleToBounds((int) c.getParent().getInitFlag(SceneInitFlag.SHADOW_RESOLUTION_ACROSS), (int) c.getParent().getInitFlag(SceneInitFlag.SHADOW_RESOLUTION_UP));
    }

    protected static final Matrix AXIS_CONV_MATRIX = new Matrix(new double[][] {
        {0, -1, 0, 0},
        {-1, 0, 0, 0},
        {0, 0, 1, 0},
        {0, 0, 0, 0}
    });

    protected static Matrix lookAt(Vector3d rightVec, Vector3d upVec, Vector3d fwdVec, Point3d pos) {
        Matrix axisMatrix = new Matrix(new double[][] {
            {rightVec.x(), rightVec.y(), rightVec.z(), 0},
            {upVec.x(), upVec.y(), upVec.z(), 0},
            {fwdVec.x(), fwdVec.y(), fwdVec.z(), 0},
            {0, 0, 0, 1}
        });
        
        Matrix pointMatrix = new Matrix(new double[][] {
            {1, 0, 0, -1 * pos.xReal()},
            {0, 1, 0, -1 * pos.yReal()},
            {0, 0, 1, -1 * pos.zReal()},
            {0, 0, 0, 1}
        });

        return Matrix.multiply(axisMatrix, pointMatrix);
    }
}
