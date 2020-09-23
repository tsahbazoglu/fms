package tr.org.tspb.logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Telman Şahbazoğlu
 */
@Singleton
public class LoggerProducer {

    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {

        Bean bean = injectionPoint.getBean();
        if (bean != null) {
            return LoggerFactory.getLogger(bean.getBeanClass());
        } else {
            //in case of njection from servlet 
            return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
        }
    }
}
