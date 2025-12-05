package rewards.internal.restaurant;

import java.beans.JavaBean;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Loads restaurant aggregates. Called by the reward network to find and reconstitute Restaurant entities from an
 * external form such as a set of RDMS rows.
 *
 * Objects returned by this repository are guaranteed to be fully-initialized and ready to use.
 */
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	/**
	 * Load a Restaurant entity by its merchant number.
	 * @param merchantNumber the merchant number
	 * @return the restaurant
	 */
	Restaurant findByNumber(String merchantNumber);
}
