/**
 * Unit tests for geometries.polygon class
 * @author Dan
 */

package geometries;

import geometries.Intersectable.GeoPoint;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import primitives.Point3D;
import primitives.Vector;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Testing Polygon
 * @author Dan
 * @author reouv and raph
 */
public class PolygonTest {

    /**
     * Test method for
     * {@link geometries.Polygon#Polygon(primitives.Point3D...)}.
     */

    @Test
    public void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct concave quadrangular with vertices in correct order
        try {
            new Polygon(new Point3D(0, 0, 1), new Point3D(1, 0, 0),
                    new Point3D(0, 1, 0), new Point3D(-1, 1, 1));
        } catch (IllegalArgumentException e) {
            fail("Failed constructing a correct polygon");
        }

        // TC02: Wrong vertices order
        try {
            new Polygon(new Point3D(0, 0, 1), new Point3D(0, 1, 0),
                    new Point3D(1, 0, 0), new Point3D(-1, 1, 1));
            fail("Constructed a polygon with wrong order of vertices");
        } catch (IllegalArgumentException e) {}

        // TC03: Not in the same plane
        try {
            new Polygon(new Point3D(0, 0, 1), new Point3D(1, 0, 0),
                    new Point3D(0, 1, 0), new Point3D(0, 2, 2));
            fail("Constructed a polygon with vertices that are not in the same plane");
        } catch (IllegalArgumentException e) {}

        // TC04: Concave quadrangular
        try {
            new Polygon(new Point3D(0, 0, 1), new Point3D(1, 0, 0),
                    new Point3D(0, 1, 0), new Point3D(0.5, 0.25, 0.5));
            fail("Constructed a concave polygon");
        } catch (IllegalArgumentException e) {}

        // =============== Boundary Values Tests ==================

        // TC10: Vertix on a side of a quadrangular
        try {
            new Polygon(new Point3D(0, 0, 1), new Point3D(1, 0, 0),
                    new Point3D(0, 1, 0), new Point3D(0, 0.5, 0.5));
            fail("Constructed a polygon with vertix on a side");
        } catch (IllegalArgumentException e) {}

        // TC11: Last point = first point
        try {
            new Polygon(new Point3D(0, 0, 1), new Point3D(1, 0, 0),
                    new Point3D(0, 1, 0), new Point3D(0, 0, 1));
            fail("Constructed a polygon with vertice on a side");
        } catch (IllegalArgumentException e) {}

        // TC12: Collocated points
        try {
            new Polygon(new Point3D(0, 0, 1), new Point3D(1, 0, 0),
                    new Point3D(0, 1, 0), new Point3D(0, 1, 0));
            fail("Constructed a polygon with vertice on a side");
        } catch (IllegalArgumentException e) {}

    }

    /**
     * Test method for {@link geometries.Polygon#getNormal(primitives.Point3D)}.
     */
    @Test
    public void testGetNormal()
    {
       //we take the four sides polygon his normal is (0.57,0.57,0.57)
        Polygon pl = new Polygon(new Point3D(0, 0, 1), new Point3D(1, 0, 0), new Point3D(0, 1, 0),
                new Point3D(-1, 1, 1));
        double sqrt3 = Math.sqrt(1d / 3); //=> sqrt3 = 0.5773......
        // if pl.gerNormal return (0.57,0.57,0.57) it's gooooooodddd
        assertEquals( new Vector(sqrt3, sqrt3, sqrt3), pl.getNormal(new Point3D(0, 1, 1)));
    }
    @Test
    public void testGetNormal_invalid()
    {
        /**
         *
         * we create a polygon who is in the X and Y axes that's mean
         * that his vector normal is a vector who is parallel to the Z axe
         */

        Polygon pl = new Polygon(new Point3D(0, 0, 0), new Point3D(3, 0, 0),
                new Point3D(3, 1, 0),new Point3D(0, 1, 0));

        //we use an invalid Vector in order to verify if its reject an exception
        try{
            assertEquals(new Vector(1, 0, 1), pl.getNormal(new Point3D(0, 1, 1)));

        }catch(AssertionError e)
        {
            // if an error has rejected here that's mean that the the two vectors are different
            return;
        }
        // if no error are catch that's mean that the assertEqual run well
        // and the two vectors are the same and they need to be different
        fail("the vector normal to the polygon need to be (0,0,1)  ");
    }
    /**
     *  EP: Three cases:
     *    • Inside polygon/triangle
     *    • Outside against edge
     *    • Outside against vertex
     */


    @org.junit.Test
    public void findIntsersections() {
        Polygon Polygon = new Polygon(new Point3D(0,0,0),
                new Point3D(0,4,0),
                new Point3D(4,4,0) ,  new Point3D(4,0,0));
        //===========================================================//
        //===================EP: Three cases:=======================//
        // TC01: Inside Polygon (1 point)
        primitives.Ray TC01 = new primitives.Ray(new primitives.Point3D(1.0,1.0,-1.0), new primitives.Vector(0.0,0.0,1.0));
        List<GeoPoint> resultTC01 = Polygon.findIntersections(TC01);

        Assertions.assertEquals( 1, resultTC01.size(),"Wrong number of points");

        Assertions.assertEquals(new primitives.Point3D(1,1,0), resultTC01.get(0).getPoint(), "Ray crosses Polygon");
        // TC021:  Outside against edge (0 point)
        primitives.Ray TC021 = new primitives.Ray(new primitives.Point3D(5.0,2.0,-1.0), new primitives.Vector(0.0,0.0,1.0));
        List<GeoPoint> resultTC021 = Polygon.findIntersections(TC021);

        Assertions.assertEquals( null, resultTC021,"Wrong number of points");

        // TC022: Outside against vertex (0 points)
        primitives.Ray TC022 = new primitives.Ray(new primitives.Point3D(5.0,-1.0,-1.0), new primitives.Vector(0.0,0.0,1.0));
        List<GeoPoint> resultTC022 = Polygon.findIntersections(TC022);

        Assertions.assertEquals( null, resultTC022,"Wrong number of points");

        //===========================================================//
        //===BVA: Three cases (the ray begins "before" the plane)===//

        // TC11:  On edge (0 points)
        primitives.Ray TC11 = new primitives.Ray(new primitives.Point3D(2.0,4.0,-1.0), new primitives.Vector(0.0,0.0,1.0));
        List<GeoPoint> resultTC11 = Polygon.findIntersections(TC11);

        Assertions.assertEquals( null, resultTC11,"Wrong number of points");

        // TC12: In vertex (0 points)
        primitives.Ray TC12 = new primitives.Ray(new primitives.Point3D(0.0,0.0,-1.0), new primitives.Vector(0.0,0.0,1.0));
        List<GeoPoint> resultTC12 = Polygon.findIntersections(TC12);

        Assertions.assertEquals( null, resultTC12,"Wrong number of points");

        // TC13: On edge's continuation (0 points)
        primitives.Ray TC13 = new primitives.Ray(new primitives.Point3D(0.0,8.0,-1.0), new primitives.Vector(0.0,0.0,1.0));
        List<GeoPoint> resultTC13 = Polygon.findIntersections(TC13);

        Assertions.assertEquals( null, resultTC13,"Wrong number of points");


    }
}
