/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clas.ftcal.tools;

import java.util.Set;
import org.clas.ft.tools.FTDetector;
import org.jlab.detector.base.DetectorCollection;
import org.jlab.detector.base.DetectorType;
import org.clas.view.DetectorShape2D;

/**
 *
 * @author devita
 */
public class FTCalDetector extends FTDetector {
    
    String viewName;
    
    private final int nCrystalX = 46;
    private final int nCrystalY = 10;
    
    private final double crystal_size = 15;
    
    private double x0=0;
    private double y0=0;
    
    private DetectorCollection<ShapePoint> points = new DetectorCollection<ShapePoint>();
    DetectorCollection<Double> thresholds = new DetectorCollection<Double>();
        

    public FTCalDetector(String name) {
        super(name);
        
        for (int component = 0; component < nCrystalX*nCrystalY; component++) {
            if(doesThisCrystalExist(component)) {
                int iy = component / nCrystalX;
                int ix = component - iy * nCrystalX;               
                double xcenter = crystal_size * (ix + 0.5);
                if (ix > nCrystalX/2-1) {
                    ix = ix -(nCrystalX/2-1);
                } else {
                    ix = ix - nCrystalX/2;
                }
                if (iy > nCrystalY/2-1) {
                    iy = iy -(nCrystalY/2-1);
                } else {
                    iy = iy - nCrystalY/2;
                }
                double ycenter = crystal_size * (nCrystalY - iy - 0.5 + 1.);
                points.add(1, 1, component, new ShapePoint(ix,iy));
                DetectorShape2D shape = new DetectorShape2D(DetectorType.FTCAL, 1, 1, component);
                shape.createBarXY(crystal_size, crystal_size);
                shape.getShapePath().translateXYZ(xcenter+x0, ycenter+y0, 0.0);
                shape.setColor(0, 145, 0);
                this.getView().addShape(name,shape);           
            }
        }
    }

    
    public void setCenter(double xCenter, double yCenter) {
        this.x0=xCenter;
        this.y0=yCenter;
    }
    
    public void addPaddles() {
        for(int ipaddle=0; ipaddle<4; ipaddle++) {
            int component = 501+ipaddle;
            points.add(1, 1, component, new ShapePoint(0,0));
            DetectorShape2D paddle = new DetectorShape2D(DetectorType.FTCAL, 1, 1, 501+ipaddle);
            paddle.createBarXY(crystal_size*nCrystalX, crystal_size/2.);
            paddle.getShapePath().translateXYZ(crystal_size*nCrystalX/2.,crystal_size*(nCrystalX+2)*(ipaddle % 2)+crystal_size/4.*(((int) ipaddle/2)*2-1),0.0);
            paddle.setColor(0, 145, 0);
            this.getView().addShape(this.getName(),paddle);
        }
    }
    
    public void addSync() {
        DetectorShape2D paddle = new DetectorShape2D(DetectorType.FTCAL, 1, 1, 500);
        points.add(1, 1, 500, new ShapePoint(0,0));
        paddle.createBarXY(crystal_size, crystal_size);
        paddle.getShapePath().translateXYZ(crystal_size*0.5,crystal_size*(22-0.5+1),0.0);
        paddle.setColor(0, 145, 0);
        this.getView().addShape(this.getName(),paddle);
    }
    
    
    public void setThresholds(double threshold) {
        for (int component : this.getDetectorComponents()) {
            thresholds.add(1, 1, component, threshold);
        }
    }
    
    public DetectorCollection<Double> getThresholds() {
        return this.thresholds;
    }
    
    public DetectorCollection getShapePoints() {
        return this.points;
    }
    
    public int getIdX(int component) {
        return this.points.get(1, 1, component).x();
    }
    
    public int getIdY(int component) {
        return this.points.get(1, 1, component).y();
    }

    public int getIX(int component) {
        int i = this.points.get(1, 1, component).x();
        if (i > 0) {
            i = i +(nCrystalX/2-1);
        } else {
            i = i + nCrystalX/2;
        }
        return i;
    }
    
    public int getIY(int component) {
        int i = this.points.get(1, 1, component).y();
        if (i > 0) {
            i = i +(nCrystalY/2-1);
        } else {
            i = i + nCrystalY/2;
        }
        return i;    
    }

    public int getComponent(int ix, int iy) {
        return iy*nCrystalX+ix; 
    }
        
    public int getComponent(double x, double y) {
        int ix = (int) ((x+nCrystalX/2*crystal_size)/crystal_size);
        int iy = (int) ((y+nCrystalY/2*crystal_size)/crystal_size);
        return iy*nCrystalX+ix; 
    }

    public String getComponentName(int component) {
        String title = "(" + this.getIdX(component) + "," + this.getIdY(component) + ")";
        return title;
    }
    
    public Set<Integer> getDetectorComponents() {
        return this.points.getComponents(1, 1);
    }
 
    public boolean hasComponent(int component) {
        return this.points.hasEntry(1, 1, component);
    }
    
    public boolean hasComponent(int ix, int iy) {
        int component = iy*nCrystalX+ix; 
        return this.points.hasEntry(1, 1, component);
    }

    public int getNComponents() {
        return this.points.getComponents(1, 1).size();
    }
    
    public int getComponentMaxCount() {
        int keyMax=0;
        for(int key : this.points.getComponents(1, 1)) keyMax=key;
        return keyMax;
    }

    public int[] getIDArray() {
        int[] crystalID = new int[this.getNComponents()];
        int ipointer=0;
        for(int component : this.points.getComponents(1, 1)) {
            crystalID[ipointer]=component;
            ipointer++;
        }
        return crystalID;
    }
    
    private boolean doesThisCrystalExist(int id) {

        boolean crystalExist=true;
        int iy = id / nCrystalX;
        int ix = id - iy * nCrystalX;

        if (ix<0 && ix>= nCrystalX) crystalExist=false;
        else if(iy<0 && iy>= nCrystalY) crystalExist=false;
        else if((ix>12 && ix<22) && (iy==4 || iy==5)) crystalExist=false;
        return crystalExist;
    }
    
    public class ShapePoint {
        private int x; // the x coordinate
        private int y; // the y coordinate
    
        public ShapePoint(int x, int y) {
            set(x, y);
        }    

        private void set(int x, int y) {
            setX(x);
            setY(y);
        }

        private void setX(int x) {
            this.x = x;
        }

        private void setY(int y) {
            this.y = y;
        }
        
        public int x() {
            return x;
        }
        
        public int y() {
            return y;
        }
    }
}
