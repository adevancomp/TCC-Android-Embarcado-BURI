package br.edu.uea.buri.repository

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.domain.Measurement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MeasurementRepository : JpaRepository<Measurement, Long> {
    fun findByEquipment(equipment: Equipment) : List<Measurement>
    @Query(
        value = """
            SELECT * FROM measurement  WHERE equipment_id = ?1 AND collection_date BETWEEN
            (
                (SELECT collection_date
                    FROM measurement
                    ORDER BY collection_date DESC
                    LIMIT 1) - INTERVAL '1 hour' * ?2 ) AND 
            (
                (SELECT collection_date
                    FROM measurement
                    ORDER BY collection_date DESC
                    LIMIT 1)
            ); """,
        nativeQuery = true
    ) fun findAllByLastHourInterval(equipmentId:String,hourDuration: Int) : List<Measurement>

    @Query(
        value = """
            SELECT * FROM measurement
                WHERE equipment_id = ?1 AND collection_date BETWEEN
                    (
                        (SELECT collection_date
                            FROM measurement
                            ORDER BY collection_date DESC
                            LIMIT 1) - INTERVAL '1 minute' * ?2 ) AND 
                    (
                        (SELECT collection_date
                            FROM measurement
                            ORDER BY collection_date DESC
                            LIMIT 1)
                    );""",
        nativeQuery = true
    )
    fun findAllByLastMinuteInterval(equipmentId:String,minuteDuration: Int): List<Measurement>

    @Query(
        value = """
            SELECT * FROM measurement
            WHERE equipment_id = ?1
            ORDER BY collection_date DESC
            LIMIT 3
        """,
        nativeQuery = true
    )
    fun findLast3Measurements(equipmentId: String) : List<Measurement>
}