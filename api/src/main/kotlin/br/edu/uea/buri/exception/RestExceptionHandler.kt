package br.edu.uea.buri.exception

import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(DataAccessException::class)
    fun handlerValidException(ex: DataAccessException) : ResponseEntity<ExceptionDetails>{
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ExceptionDetails(
                    title = "Bad Request! Consulte a documentação em /swagger-ui.html",
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.CONFLICT.value(),
                    exception = ex.javaClass.toString(),
                    details = mutableMapOf(ex.cause.toString() to ex.message)
                )
            )
    }
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handlerUsernameNotFoundException(ex: UsernameNotFoundException) : ResponseEntity<ExceptionDetails>{
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ExceptionDetails(
                    title = "Not Found! Consulte a documentação em /swagger-ui.html",
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.NOT_FOUND.value(),
                    exception = ex.javaClass.toString(),
                    details = mutableMapOf(ex.cause.toString() to ex.message)
                )
            )
    }
    @ExceptionHandler(DomainException::class)
    fun handlerValidException(ex: DomainException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ExceptionDetails(
                    title = "Bad Request! Consult the documentation",
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    exception = ex.javaClass.toString(),
                    details = mutableMapOf(ex.cause.toString() to ex.message)
                )
            )
    }

    @ExceptionHandler(SensorNotConnectedException::class)
    fun handlerSensorNotConnectedException(ex: SensorNotConnectedException) : ResponseEntity<String>{
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Sensores não conectados!!")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerValidException(ex: MethodArgumentNotValidException) : ResponseEntity<ExceptionDetails>{
        val errors : MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.stream().forEach {
            error: ObjectError ->
                val fieldName: String = (error as FieldError).field
                val messageError: String? = error.defaultMessage
                errors[fieldName] = messageError
        }

        return ResponseEntity(
            ExceptionDetails(
                title = "Bad Request! Consulte a documentação em /swagger-ui.html",
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = ex.objectName,
                details = errors
            ), HttpStatus.BAD_REQUEST
        )
    }
}