package com.ijurnove.cpu3d;
import java.io.BufferedReader;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * <code>ObjReader</code> contains one method, <code>read()</code>, for converting an .obj file to a <code>Mesh</code>. 
 */
public class ObjReader {
    /**
     * Reads a given .obj file and returns a <code>Mesh</code> of its triangles.
     * @param path the path of the file to be read
     * @return a Mesh representing the object defined in the .obj file
     */
    public static Mesh read(String path) {
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LinkedList<String[]> linesList = new LinkedList<>();
        
        reader.lines()
            .filter(s -> !s.isEmpty())
            .forEach(s -> {
                String[] split = s.split(" +");
                if (
                    split[0].equals("v") ||
                    split[0].equals("vn") ||
                    split[0].equals("f")) {
                    linesList.add(split);
                }
                
                if (split[0].equals("vt")) {
                    linesList.add(
                        new String[] {
                            split[0],
                            split[1],
                            split[2],
                            "EMPTY"
                        }
                    );
                }
            }
        );

        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[][] lines = linesList.toArray(String[][]::new);
        
        Point3d[] vertices = Arrays.stream(lines)
            .filter(s -> s[0].equals("v"))
            .map(s -> 
                new Point3d(
                    parseDouble(s[1]),
                    parseDouble(s[2]),
                    parseDouble(s[3])
                )
            )
            .toArray(Point3d[]::new);
            
        Vector3d[] vertexNormals = Arrays.stream(lines)
            .filter(s -> s[0].equals("vn"))
            .map(s -> 
                new Vector3d(
                    parseDouble(s[1]),
                    parseDouble(s[2]),
                    parseDouble(s[3])
                )
            )
            .toArray(Vector3d[]::new);

        UV[] vertexTextures = Arrays.stream(lines)
            .filter(s -> s[0].equals("vt"))
            .map(s -> 
                new UV(
                    parseDouble(s[1]),
                    parseDouble(s[2])
                )
            )
            .toArray(UV[]::new);

        LinkedList<Triangle> triangleList = new LinkedList<>();

        Arrays.stream(lines)
            .filter(s -> s[0].equals("f"))
            .forEach(fullArgs -> {
                // cut off first element ("f")
                String[] s = new String[fullArgs.length - 1];
                for (int i = 0; i < s.length; i++) {
                    s[i] = fullArgs[i+1];
                }
                
                PointArgs[] args = Arrays.stream(s)
                    .map(s2 -> {
                        int[] argSet = Arrays.stream(s2.split("/"))
                            .mapToInt(singleArg -> Integer.parseInt(singleArg) - 1)
                            .toArray();
                        return new PointArgs(argSet[0], argSet[1], argSet[2]);
                    })
                    .toArray(PointArgs[]::new);

                for (int i = 1; i <= (args.length - 2); i++) {
                    Triangle tri = new Triangle(
                        new Point3d[] {
                            vertices[args[0].vertex()],
                            vertices[args[i].vertex()],
                            vertices[args[i+1].vertex()]
                        }
                    );

                    tri.setPointNormals(
                        vertexNormals[args[0].normal()],
                        vertexNormals[args[i].normal()],
                        vertexNormals[args[i+1].normal()]
                    );

                    tri.setTextureCoords(
                        vertexTextures[args[0].uv()],
                        vertexTextures[args[i].uv()],
                        vertexTextures[args[i+1].uv()]
                    );

                    triangleList.add(tri);
                }
            });

        return new Mesh(triangleList.toArray(Triangle[]::new));
    }
}


class PointArgs {
    private final int vertex;
    private final int normal;
    private final int uv;

    public PointArgs(int vertex, int uv, int normal) {
        this.vertex = vertex;
        this.uv = uv;
        this.normal = normal;
    }

    public int vertex() { return vertex; }
    public int normal() { return normal; }
    public int uv() { return uv; }
}