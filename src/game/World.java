package game;

import base.Shape;
import base.SShader;
import game.components.Component;
import game.components.ComponentHandler;
import game.components.PlayerMovement;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class World {
    public ArrayList<ob2D> ob2Ds;
    public ArrayList<PointLight> pointLights;
    public EnvironmentLight envLight;

    public HashMap<Shape, HashMap<ob2D,Boolean>> modelObpair;

    public GMap gm;

    int time;

    public SShader sceneShader;

    public World() {
        modelObpair = new HashMap<Shape, HashMap<ob2D,Boolean>>();
        sceneShader=new SShader("src/vertex.glsl","src/fragment.glsl");
        pointLights=new ArrayList<PointLight>();
        time=0;
    }
    public void init()
    {
            float k=1f;
            pointLights.add(new PointLight(new Vector2f(-k,0f),new Vector3f(1),1f,2f));
            pointLights.add(new PointLight(new Vector2f(k,0f),new Vector3f(1),1f,5f));
            pointLights.add(new PointLight(new Vector2f(0f,k),new Vector3f(1),1f,5f));
            pointLights.add(new PointLight(new Vector2f(0f,-k),new Vector3f(1),1f,5f));
            envLight=new EnvironmentLight(new Vector3f(1),1f);

            sceneShader.use();
            sceneShader.addPointLights(pointLights);
            sceneShader.updateEnvironmentLight(envLight);

            gm.ts.use();
            gm.ts.addPointLights(pointLights);
            gm.ts.updateEnvironmentLight(envLight);
    }
    public void deleteOb2D(ob2D b)
    {
        b.delete();
        modelObpair.get(b.sh).remove(b);
    }
    public void update() {

        /*
        for (LoopAnimation ba : ComponentHandler.getAllByComponent(LoopAnimation.class))
            ba.update();



        ComponentHandler.getAllByComponent(CameraController.class).get(0).update();
        */
        //time+=1;
        envLight.intensity=0.5f*Math.abs((float)Math.sin(time*0.001f));
        gm.ts.use();
        gm.ts.updateEnvironmentLight(envLight);
        gm.ts.stop();

        sceneShader.use();
        sceneShader.updateEnvironmentLight(envLight);
        sceneShader.stop();

        PlayerMovement pm = (PlayerMovement)ComponentHandler.getAllByComponent(PlayerMovement.class).get(0);
       // pm.update();

        ob2D p =pm.parent;

        for(Map.Entry<String,ArrayList<Component>> et:ComponentHandler.database.entrySet())
        {
            for(Component c:et.getValue())
            {
                if(c.enabled)
                    c.update();
            }
        }

        for (int i = 1; i < ob2Ds.size(); i++)
            BPhysics.handlePlayerFOCollision(p, ob2Ds.get(i));

        time+=1;
    }

}
