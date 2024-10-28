package onion4.infrastructure.repository.hibernaterepo;

import onion4.infrastructure.repository.entities.HibernateNotification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HibernateNotificationRepository extends CrudRepository<HibernateNotification, Integer> {

}
