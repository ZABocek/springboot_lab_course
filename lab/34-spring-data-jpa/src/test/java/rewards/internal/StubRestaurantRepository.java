package rewards.internal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.ObjectRetrievalFailureException;

import rewards.Dining;
import rewards.internal.account.Account;
import rewards.internal.restaurant.BenefitAvailabilityPolicy;
import rewards.internal.restaurant.Restaurant;
import rewards.internal.restaurant.RestaurantRepository;

import common.money.Percentage;

/**
 * A dummy restaurant repository implementation. Has a single restaurant "Apple Bees" with a 8% benefit availability
 * percentage that's always available.
 * 
 * Stubs facilitate unit testing. An object needing a RestaurantRepository can work with this stub and not have to bring
 * in expensive and/or complex dependencies such as a Database. Simple unit tests can then verify object behavior by
 * considering the state of this stub.
 */
@SuppressWarnings({ "null", "unchecked", "rawtypes" })
public class StubRestaurantRepository implements RestaurantRepository {

	private Map<String, Restaurant> restaurantsByMerchantNumber = new HashMap<String, Restaurant>();

	public StubRestaurantRepository() {
		Restaurant restaurant = new Restaurant("1234567890", "Apple Bees");
		restaurant.setBenefitPercentage(Percentage.valueOf("8%"));
		restaurant.setBenefitAvailabilityPolicy(new AlwaysReturnsTrue());
		restaurantsByMerchantNumber.put(restaurant.getNumber(), restaurant);
	}

	public Restaurant findByNumber(String merchantNumber) {
		Restaurant restaurant = (Restaurant) restaurantsByMerchantNumber.get(merchantNumber);
		if (restaurant == null) {
			throw new ObjectRetrievalFailureException(Restaurant.class, merchantNumber);
		}
		return restaurant;
	}

	/**
	 * A simple "dummy" benefit availability policy that always returns true. Only useful for testing--a real
	 * availability policy might consider many factors such as the day of week of the dining, or the account's reward
	 * history for the current month.
	 */
	private static class AlwaysReturnsTrue implements BenefitAvailabilityPolicy {
		public boolean isBenefitAvailableFor(Account account, Dining dining) {
			return true;
		}
	}

	// Stub implementations for inherited JpaRepository methods (not used in tests)
	@Override public void flush() {}
	@Override public <S extends Restaurant> S saveAndFlush(S entity) { return entity; }
	@Override public <S extends Restaurant> java.util.List<S> saveAllAndFlush(Iterable<S> entities) { return new java.util.ArrayList<>(); }
	@Override public void deleteAllInBatch(Iterable<Restaurant> entities) { // Stub - not used in tests
	}
	@Override public void deleteAllByIdInBatch(Iterable<Long> ids) { // Stub - not used in tests
	}
	@Override public void deleteAllInBatch() { // Stub - not used in tests
	}
	@Override public Restaurant getById(Long id) { return null; }
	@Override public Restaurant getReferenceById(Long id) { return null; }
	@Override public Restaurant getOne(Long id) { return null; }
	@Override public <S extends Restaurant> S save(S entity) { return entity; }
	@Override public <S extends Restaurant> java.util.List<S> saveAll(Iterable<S> entities) { return new java.util.ArrayList<>(); }
	@Override public java.util.Optional<Restaurant> findById(Long id) { return java.util.Optional.empty(); }
	@Override public boolean existsById(Long id) { return false; }
	@Override public java.util.List<Restaurant> findAll() { return new java.util.ArrayList<>(); }
	@Override public java.util.List<Restaurant> findAllById(Iterable<Long> ids) { return new java.util.ArrayList<>(); }
	@Override public long count() { return 0; }
	@Override public void deleteById(Long id) { // Stub - not used in tests
	}
	@Override public void delete(Restaurant entity) { // Stub - not used in tests
	}
	@Override public void deleteAllById(Iterable<? extends Long> ids) { // Stub - not used in tests
	}
	@Override public void deleteAll(Iterable<? extends Restaurant> entities) { // Stub - not used in tests
	}
	@Override public void deleteAll() { // Stub - not used in tests
	}
	@Override public java.util.List<Restaurant> findAll(org.springframework.data.domain.Sort sort) { return new java.util.ArrayList<>(); }
	@Override public org.springframework.data.domain.Page<Restaurant> findAll(org.springframework.data.domain.Pageable pageable) { return null; }
	@Override public <S extends Restaurant> java.util.List<S> findAll(org.springframework.data.domain.Example<S> example) { return new java.util.ArrayList<>(); }
	@Override public <S extends Restaurant> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) { return null; }
	@Override public <S extends Restaurant> java.util.List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) { return new java.util.ArrayList<>(); }
	@Override public <S extends Restaurant> long count(org.springframework.data.domain.Example<S> example) { return 0; }
	@Override public <S extends Restaurant> boolean exists(org.springframework.data.domain.Example<S> example) { return false; }
	@Override public <S extends Restaurant> java.util.Optional<S> findOne(org.springframework.data.domain.Example<S> example) { return java.util.Optional.empty(); }
	@Override public <S extends Restaurant, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
}