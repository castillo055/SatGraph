import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import KBControlsFX.*;

public class Main extends Application {

    AnchorPane root;
    Group system;
    PerspectiveCamera cam;

    @Override
    public void start(Stage stage) throws Exception {
        root = new AnchorPane();
        system = new Group();
        Rotate phi = new Rotate();
        phi.setAxis(new Point3D(1, 0, 0));
        Rotate theta = new Rotate();
        theta.setAxis(new Point3D(0, 0, 1));
        system.getTransforms().addAll(phi, theta);
        root.getChildren().add(system);

        cam = new PerspectiveCamera();
        cam.setTranslateZ(-100.0);

        Scene scene = new Scene(root, 1480, 820, true, SceneAntialiasing.DISABLED);
        scene.setFill(new Color(0.05, 0.05, 0.1, 1.0));
        scene.setCamera(cam);

        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("SatGraph");
        stage.show();

        addEarth();
        //addOrbit(0, 0, 0.5, 0, 0, 1000);

        new Thread(() -> {
            try {
                loadData();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

        Controls.init(root);
        Controls.addActionAndBind(KeyCode.A, "move left", () -> system.setTranslateX(system.getTranslateX() - 10));
        Controls.addActionAndBind(KeyCode.D, "move right", () -> system.setTranslateX(system.getTranslateX() + 10));
        Controls.addActionAndBind(KeyCode.W, "move up", () -> system.setTranslateY(system.getTranslateY() - 10));
        Controls.addActionAndBind(KeyCode.S, "move down", () -> system.setTranslateY(system.getTranslateY() + 10));

        Controls.addActionAndBind(KeyCode.UP, "rotate up", () -> phi.setAngle(phi.getAngle()+2.0));
        Controls.addActionAndBind(KeyCode.DOWN, "rotate down", () -> phi.setAngle(phi.getAngle()-2.0));
        Controls.addActionAndBind(KeyCode.LEFT, "rotate left", () -> theta.setAngle(theta.getAngle()-2.0));
        Controls.addActionAndBind(KeyCode.RIGHT, "rotate right", () -> theta.setAngle(theta.getAngle()+2.0));

        Controls.addActionAndBind(KeyCode.PLUS, "zoom in", () -> deltaZoom(10));
        Controls.addActionAndBind(KeyCode.MINUS, "zoom out", () -> deltaZoom(-10));
    }

    public void deltaZoom(double dZ){
        /*system.setScaleX(system.getScaleX()*dZ);
        system.setScaleY(system.getScaleY()*dZ);
        system.setScaleZ(system.getScaleZ()*dZ);*/
        cam.setTranslateZ(cam.getTranslateZ()+dZ);
    }

    public void addEarth(){
        Sphere earth = new Sphere(150.0);
        earth.setRotationAxis(new Point3D(1,0,0));
        earth.setRotate(90.0);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass().getResource("earth.jpg").toExternalForm()));

        earth.setMaterial(material);
        system.getChildren().add(earth);
    }

    public void addOrbit(double inc, double RAAN, double e, double argP, double meanA, double meanM){
        double T = 1.0/meanM;
        double a = Math.cbrt(10096049481882.14934782137610050100 * Math.pow(T, 2));
        double b = Math.sqrt(Math.pow(a, 2) * (1 - Math.pow(e, 2)));

        Ellipse orbit = new Ellipse();
        orbit.setSmooth(true);
        orbit.setCenterX(0);
        orbit.setCenterY(0);
        orbit.setRadiusX(a);
        orbit.setRadiusY(b);
        //orbit.setFill(Color.LIMEGREEN);
        orbit.setFill(Color.TRANSPARENT);
        orbit.setStroke(Color.LIMEGREEN);

        Rotate argPRot = new Rotate();
        argPRot.setAngle(argP);
        argPRot.setAxis(new Point3D(0,0,1));

        Rotate incRot = new Rotate();
        incRot.setAngle(inc);
        incRot.setAxis(new Point3D(0,1,0));

        Rotate RAANRot = new Rotate();
        RAANRot.setAngle(RAAN);
        //RAANRot.setAxis(new Point3D(Math.cos(Math.toRadians(inc)),0,Math.sin(Math.toRadians(inc))));
        try {
            RAANRot.setAxis(incRot.inverseTransform(new Point3D(0,0,-1)));
        } catch (NonInvertibleTransformException nonInvertibleTransformException) {
            nonInvertibleTransformException.printStackTrace();
        }


        Transform transform = incRot.createConcatenation(RAANRot).createConcatenation(argPRot);
        Translate translate = new Translate(a*e, 0, 0);

        transform = transform.createConcatenation(translate);
        orbit.getTransforms().add(transform);

        system.getChildren().add(orbit);
    }

    public void loadData() throws FileNotFoundException {
        File dataset = new File("dataset");
        Scanner fileSc = new Scanner(dataset);

        int n = 300; int i = 0;
        while(fileSc.hasNextLine() && i < n){
            // Using TLE format (cero: name; first: identifiers; second: orbital elements)
            String cero = fileSc.nextLine();
            String first = fileSc.nextLine();
            String second = fileSc.nextLine();

            String[] data = second.split(" +");

            double inc = Double.parseDouble(data[2]);
            double RAAN = Double.parseDouble(data[3]);
            double e = Double.parseDouble("0."+data[4]);
            double argP = Double.parseDouble(data[5]);
            double meanA = Double.parseDouble(data[6]);
            double meanM = Double.parseDouble(data[7]);
            System.out.println(100.0*(double)i/(double)n);
            Platform.runLater(() -> addOrbit(inc, RAAN, e, argP, meanA, meanM*100));
            /*try {
                Thread.sleep(0, 5);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }*/

            i++;
        }
    }

    public static void main(String... args){
        launch(args);
    }
}
