package edu.brown.cs.student.main.csv.parsing.examplecreatorfromrows;

/** A class for representing people and their star signs. */
public class StarSign {

  String name;
  String sign;

  /**
   * The constructor of a StarSign.
   *
   * @param name representing the first name of the person whose star sign is of interest
   * @param sign representing name's star sign
   * @throws IllegalArgumentException if either name or sign is null or empty
   */
  public StarSign(String name, String sign) throws IllegalArgumentException {
    if ((name == null) | (name.isEmpty())) {
      throw new IllegalArgumentException("Name cannot be null or empty.");
    } else {
      this.name = name;
    }

    if ((sign == null) | (sign.isEmpty())) {
      throw new IllegalArgumentException("Sign cannot be null or empty.");
    } else {
      this.sign = sign;
    }
  }

  /**
   * Method for determining equality of StarSigns.
   *
   * @param o the object to compare this to
   * @return true if o represents the same StarSign as this and false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof StarSign other)) {
      return false;
    }
    return this.name.equals(other.name) & this.sign.equals(other.sign);
  }

  /**
   * Method for determining hash code of this.
   *
   * @return the hashcode of this
   */
  @Override
  public int hashCode() {
    int result = this.name.hashCode();
    result = 31 * result + this.sign.hashCode();
    return result;
  }

  /**
   * Method for translating StarSign to String representation.
   *
   * @return the String representation of this
   */
  @Override
  public String toString() {
    return "StarSign[" + name + ", " + sign + "]";
  }
}
