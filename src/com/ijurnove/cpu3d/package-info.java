/**
 * To generate a 3D render, you must have a <code>Scene</code> containing a set of <code>Shape3d</code>s, a set of <code>Light</code>s, and a <code>Camera</code>.
 * <p>
 * This package comes with a built-in .obj file reader to generate <code>Mesh</code>es and a image reader to generate textures, both of which are required for a 
 * <code>Shape3d</code>. <code>Material</code>s are required as well to define how the <code>Shape3d</code> interacts with light. A few example <code>Material</code>s have
 * already been made, but more can be created easily.
 * <p>
 * In addition to a <code>Shape3d[]</code>, a <code>Light[]</code>, and a <code>Camera</code>, a <code>SceneInitFlags</code> object must be created to define things
 * such as shadow resolution and image size. After getting all of these and constructing a <code>Scene</code>, it can be rendered with the <code>.render()</code> method.
 */
package com.ijurnove.cpu3d;