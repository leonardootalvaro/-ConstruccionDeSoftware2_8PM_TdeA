package app.domain.ports;

import app.domain.models.Order;

public interface OrderPort {
    public boolean existsByNumber(String numString);

    public void save(Order ord);
}
