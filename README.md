# LowPoly
Low-Poly style game written in Java using LWJGL and Intellij IDE.

The rendering engine uses VAOs and VBOs which are loaded into the shaders along with the Projection and View matrices. Individual entities using the same model are looped after the model has been loaded, then a new transformation matrix is loaded to the shaders for each entity. The shaders outputs the color and depth information to the FBO pipeline until the final FBO, which resolves the multisampled FBO for smoother edges, is sent to LWJGL's Display for placement on the screen.

The engine includes shadow mapping, a simple ssao effect, and simple lighting.

![GIF](https://thumbs.gfycat.com/SecondTartAlaskankleekai-size_restricted.gif)
![In-Game Screenshot](https://github.com/ninthworld/LowPoly/blob/master/screenshots/screenshot1.png)
