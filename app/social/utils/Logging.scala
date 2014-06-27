package social.utils

import play.api.Logger
import org.slf4j.LoggerFactory

/**
 */
trait Logging {
    def loggerName: String = this.getClass.getName
    lazy val logger = LoggerFactory.getLogger(loggerName)
}
