/*
 * 
 * Vector.java
 *
 * Created on February 18, 2008, 5:33 PM
 *
 * 
 */


/**
 * 
 * A 2d vector defined as its X and Y components. Used in velocity, mostly.
 * @author Steven
 */
public class Vector {
    
    /**
     * The X component
     */
    private float x;
    /**
     * The Y component
     */
    private float y;

	/**
	 * Constructor called with no arguments produces a vector with
	 * components equal to zero. good for setting velocity to nothing.
	 */
    public Vector() {
	setXY(0,0);
    }
    
	/**
	 * creates new Vector.
	 * @param x X component to be set
	 * @param y Y component to be set
	 */
    public Vector(float x, float y) {
	setXY(x,y);
    }
    
    /**
	 * creates new Vector with doubles.
	 * @param x X component to be set
	 * @param y Y component to be set
	 */
    public Vector(double x, double y) {
	setXY((float)x,(float)y);
    }
    
    
	/**
	 * Gets the X component
	 * @return X component
	 */
    public float getX() {
	return x;
    }

	/**
	 * Sets the X component
	 * @param x the value X will be set to
	 */
    public void setX(float x) {
	this.x = x;
    }

	/**
	 * Gets the Y component
	 * @return Y component
	 */
    public float getY() {
	return y;
    }

	/**
	 * Sets Y component
	 * @param y What to set the Y component to
	 */
    public void setY(float y) {
	this.y = y;
    }
    
	/**
	 * Set both the X and Y component more easily
	 * @param x what to set the X component to
	 * @param y what to set the Y component to
	 */
    public void setXY( float x, float y) {
	setX(x);
	setY(y);
    }

	/**
	 * Sets this vector equal to the one specified
	 * @param v what to set this vector to
	 */
    public void setVector( Vector v ) {
	setX(v.getX());
	setY(v.getY());
    }
    
	/**
	 * Gets the magnitude of the vector, basically the hypotenuse
	 * of the triangle made.
	 * @return Magnitude of the vector
	 */
    public float getMagnitude() {
	return (float)Math.sqrt( Math.pow(x, 2) + Math.pow(y, 2) );
    }
    
	/**
	 * Gets the angle (theta) of the vector.
	 * @return Direction in radians
	 */
    public float getDirection() {
	return (float)Math.atan2( y, x );
    }
    
	/**
	 * Returns the vector with a magnitude of 1 and the same
	 * direction as this one
	 * @return The unit vector of this one
	 */
    public Vector getUnitVector() {
	return new Vector( x/getMagnitude(), y/getMagnitude() );
    }
    
	/**
	 * Adds the two given Vectors together, component by component
	 * @return The sum of the Vectors
	 * @param v One of the Vectors to add
	 * @param u The other vector to add
	 */
    public static Vector add( Vector v, Vector u) {
	return new Vector( v.getX()+u.getX(), v.getY()+u.getY() );
    }
	/**
	 * Adds given vector to this one
	 * @param v The vector to add to this one
	 */
	public void add( Vector v ) {
	   setVector( Vector.add( this, v) );
	}

	/**
	 * Multiplies the given vector by the given scalar
	 * @param v The Vector to multiply
	 * @param s The scalar to multiply by
	 * @return The resulting Vector
	 */
    public static Vector multiply( Vector v, float s ) {
	return new Vector( v.getX()*s, v.getY()*s );
    }
	
	/**
	 * Multipies this vector by the given scalar
	 * @param s Scalar to multiply this vector by
	 */
	public void multiply( float s ) {
	    setVector( Vector.multiply(this, s) );
	}
	
	/**
	 * Returns the dot product of the two vectors. Returns a
	 * vector with the product of the X components and the
	 * product of the Y components.
	 * @param v One Vector
	 * @param u The other Vector
	 * @return The resulting scalar
	 */
    public static float dotProduct( Vector v, Vector u ) {
	return u.getX() * v.getX() + u.getY() * v.getY();
    }

	/**
	 * Returns the dot product of this vector and the parameter
	 * @param v Vector to multiply by
	 * @return The resulting Scalar
	 */
	public float dotProduct( Vector v ) {
	    return Vector.dotProduct(this, v);
	}
	
	/**
	 * Returns the magnitude of the cross product
	 * of the two parameters. Since this is a 2d vector,
	 * it can't return the whole vector, which would
	 * be in a different plane.
	 * @return the scalar magnitude of the cross product
	 * @param v one vector
	 * @param u the other one
	 */
    public static float crossProduct( Vector v, Vector u) {
	return u.getMagnitude()*v.getMagnitude()*(float)Math.sin( u.getDirection() - v.getDirection() );
    }

	/**
	 * Returns the magnitude of the cross product of this vector
	 * and the parameter
	 * @param v the vector to multiply by
	 * @return the resulting scalar
	 */
	public float crossProduct( Vector v ) {
	    return Vector.crossProduct(this, v);
	}
}
