package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Order;
import app.domain.models.OrderItem;
import app.domain.models.ItemType;
import app.domain.ports.OrderPort;

import java.util.HashSet;
import java.util.Set;

public class CreateOrder {

    private OrderPort port;

    public CreateOrder(OrderPort port) {
        this.port = port;
    }

    public void createOrder(Order ord) throws BusinessException {
        if (ord.getId() > 0 && port.existsByNumber(String.valueOf(ord.getId()))) {
            throw new BusinessException("El numero de orden ya se encuentra registrado.");
        }

        if (ord.getPatient() == null) {
            throw new BusinessException("El paciente es obligatorio para la orden.");
        }
        if (ord.getDoctor() == null) {
            throw new BusinessException("El medico es obligatorio para la orden.");
        }
        if (ord.getOrderItems() == null || ord.getOrderItems().length == 0) {
            throw new BusinessException("La orden debe incluir por lo menos un item.");
        }

        boolean containsDiagnostic = false;
        boolean containsMedOrProc = false;
        Set<Long> processedIds = new HashSet<>();
        long secuenciaItem = 1;

        for (OrderItem elem : ord.getOrderItems()) {
            elem.setId(secuenciaItem++);

            if (elem.getItem() == null || elem.getItemType() == null) {
                throw new BusinessException("Se encontro un item invalido.");
            }

            if (elem.getItemType() == ItemType.MEDICALSUPPORT) {
                containsDiagnostic = true;
            } else if (elem.getItemType() == ItemType.MEDICINE || elem.getItemType() == ItemType.PROCEDURE) {
                containsMedOrProc = true;
            }

            if (!processedIds.add(elem.getItem().getId())) {
                throw new BusinessException("Existen items duplicados en la orden actual.");
            }
        }

        if (containsDiagnostic && containsMedOrProc) {
            throw new BusinessException(
                    "Hay incompatibilidad entre ayudas diagnosticas y medicamentos o procedimientos.");
        }

        port.save(ord);
    }
}
