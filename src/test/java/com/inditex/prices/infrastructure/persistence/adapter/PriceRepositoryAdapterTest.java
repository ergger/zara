package com.inditex.prices.infrastructure.persistence.adapter;

import com.inditex.prices.domain.model.Money;
import com.inditex.prices.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PriceRepositoryAdapter.class)
class PriceRepositoryAdapterTest {
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

    @Autowired
    private PriceRepositoryAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    private Price price(long id, int priceList, String amount) {
        return new Price(id, 1, 35455, priceList, START, END, 0, new Money(new BigDecimal(amount), "EUR"));
    }

    @Test
    void shouldFindApplicablePrice() {
        Optional<Price> found = adapter.findApplicablePrice(35455, 1, LocalDateTime.of(2020, 6, 14, 10, 0));

        assertTrue(found.isPresent());
        assertEquals(1, found.get().priceList());
        assertEquals(new BigDecimal("35.50"), found.get().money().amount());
        assertEquals("EUR", found.get().money().currency());
    }

    @Test
    void shouldFindApplicablePriceWithHighestPriority() {
        Optional<Price> found = adapter.findApplicablePrice(35455, 1, LocalDateTime.of(2020, 6, 15, 16, 30));

        assertTrue(found.isPresent());
        assertEquals(4, found.get().priceList());
        assertEquals(new BigDecimal("38.95"), found.get().money().amount());
    }

    @Test
    void shouldReturnEmptyWhenNoApplicablePriceExists() {
        assertTrue(adapter.findApplicablePrice(999999, 1, START).isEmpty());
    }

    @Test
    void shouldFindByNaturalKey() {
        Optional<Price> found = adapter.findByNaturalKey(1, 35455, START, END);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().priceList());
        assertEquals(new BigDecimal("35.50"), found.get().money().amount());
    }

    @Test
    void shouldReturnEmptyForUnknownNaturalKey() {
        assertTrue(adapter.findByNaturalKey(1, 35455, START, LocalDateTime.of(2030, 1, 1, 0, 0)).isEmpty());
    }

    @Test
    void shouldInsertNewPrice() {
        LocalDateTime start = LocalDateTime.of(2021, 3, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2021, 3, 31, 23, 59, 59);
        Price toSave = new Price(2, 77777, 9, start, end, 5, new Money(new BigDecimal("12.34"), "USD"));

        Price saved = adapter.save(toSave);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(saved.id());
        assertEquals(77777, saved.productId());
        assertEquals(new BigDecimal("12.34"), saved.money().amount());
        assertEquals("USD", saved.money().currency());
        assertTrue(adapter.findByNaturalKey(2, 77777, start, end).isPresent());
    }

    @Test
    void shouldUpdateExistingPrice() {
        Optional<Price> existing = adapter.findByNaturalKey(1, 35455, START, END);
        assertTrue(existing.isPresent());

        Price current = existing.get();
        Price changed = new Price(current.id(), current.brandId(), current.productId(), current.priceList(),
                current.startDate(), current.endDate(), current.priority(),
                new Money(new BigDecimal("41.75"), current.money().currency()));

        Price saved = adapter.save(changed);
        entityManager.flush();
        entityManager.clear();

        assertEquals(current.id(), saved.id());
        assertEquals(new BigDecimal("41.75"), saved.money().amount());
        assertEquals(new BigDecimal("41.75"),
                adapter.findByNaturalKey(1, 35455, START, END).get().money().amount());
    }
}