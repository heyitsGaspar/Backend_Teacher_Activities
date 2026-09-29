package com.example.children_activities.status.service;

import com.example.children_activities.activities.entity.Activity;
import com.example.children_activities.activities.repository.ActivityRepository;
import com.example.children_activities.children.entity.Child;
import com.example.children_activities.children.repository.ChildRepository;
import com.example.children_activities.status.dto.StatusResponse;
import com.example.children_activities.submissions.entity.Submission;
import com.example.children_activities.submissions.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Servicio encargado de construir el estado
 * de las actividades de todos los alumnos.
 *
 * La idea principal es:
 *
 * ALUMNOS x ACTIVIDADES
 *
 * Ejemplo:
 *
 * 4 alumnos
 * 5 actividades
 *
 * = 20 combinaciones.
 *
 * Para cada combinación preguntamos:
 *
 * ¿Existe una Submission?
 *
 * Sí  -> Entregado
 * No  -> Pendiente
 */
@Service
public class StatusService {

    private final ChildRepository childRepository;
    private final ActivityRepository activityRepository;
    private final SubmissionRepository submissionRepository;

    /**
     * Constructor donde Spring inyecta
     * los repositories que necesitamos.
     */
    public StatusService(
            ChildRepository childRepository,
            ActivityRepository activityRepository,
            SubmissionRepository submissionRepository
    ) {
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
        this.submissionRepository = submissionRepository;
    }

    /**
     * Obtiene el estado general de alumnos
     * frente a todas las actividades.
     *
     * También permite aplicar filtros.
     *
     * @param subjectId filtro opcional por asignatura
     * @param activityId filtro opcional por actividad
     * @param studentName filtro opcional por nombre
     * @param submitted filtro opcional por estado
     *
     * @return lista con el estado de cada alumno
     *         frente a cada actividad
     */
    @Transactional(readOnly = true)
    public List<StatusResponse> findStatus(
            UUID subjectId,
            UUID activityId,
            String studentName,
            Boolean submitted
    ) {

        /*
         * 1.
         *
         * Obtenemos TODOS los alumnos.
         *
         * Esto es importante porque queremos mostrar
         * también alumnos que no hayan entregado.
         */
        List<Child> children = childRepository.findAll();

        /*
         * 2.
         *
         * Obtenemos TODAS las actividades.
         *
         * Por ejemplo:
         *
         * Actividad 1
         * Actividad 2
         * Actividad 3
         * Actividad 4
         * Actividad 5
         */
        List<Activity> activities = activityRepository.findAll();

        /*
         * 3.
         *
         * Obtenemos las entregas existentes.
         *
         * Aquí solamente estarán los alumnos
         * que realmente hayan entregado.
         */
        List<Submission> submissions = submissionRepository.findAll();

        /*
         * 4.
         *
         * Creamos una lista donde iremos construyendo
         * el resultado final.
         */
        List<StatusResponse> result = new ArrayList<>();

        /*
         * 5.
         *
         * Recorremos todos los alumnos.
         */
        for (Child child : children) {

            /*
             * 6.
             *
             * Si el usuario envió studentName,
             * filtramos los alumnos por nombre.
             *
             * Ejemplo:
             *
             * ?studentName=Manuel
             *
             * Solo continuará Manuel.
             */
            if (studentName != null
                    && !studentName.isBlank()
                    && !child.getName()
                    .toLowerCase()
                    .contains(studentName.toLowerCase())) {

                continue;
            }

            /*
             * 7.
             *
             * Por cada alumno recorremos
             * todas las actividades.
             *
             * Aquí estamos creando:
             *
             * Juan + Actividad 1
             * Juan + Actividad 2
             * Juan + Actividad 3
             *
             * Manuel + Actividad 1
             * Manuel + Actividad 2
             * ...
             */
            for (Activity activity : activities) {

                /*
                 * 8.
                 *
                 * Si se proporcionó subjectId,
                 * verificamos que la actividad pertenezca
                 * a esa asignatura.
                 */
                if (subjectId != null
                        && !activity.getSubject()
                        .getId()
                        .equals(subjectId)) {

                    continue;
                }

                /*
                 * 9.
                 *
                 * Si se proporcionó activityId,
                 * solamente trabajamos con esa actividad.
                 */
                if (activityId != null
                        && !activity.getId().equals(activityId)) {

                    continue;
                }

                /*
                 * 10.
                 *
                 * Buscamos si existe una Submission
                 * para este alumno y esta actividad.
                 *
                 * Ejemplo:
                 *
                 * Juan + Actividad 1
                 */
                Submission foundSubmission = null;

                for (Submission submission : submissions) {

                    /*
                     * Comprobamos que la Submission
                     * pertenezca al alumno actual.
                     */
                    boolean sameChild =
                            submission.getChild()
                                    .getId()
                                    .equals(child.getId());

                    /*
                     * Comprobamos que la Submission
                     * pertenezca a la actividad actual.
                     */
                    boolean sameActivity =
                            submission.getActivity()
                                    .getId()
                                    .equals(activity.getId());

                    /*
                     * Si coinciden ambos:
                     *
                     * tenemos una entrega.
                     */
                    if (sameChild && sameActivity) {
                        foundSubmission = submission;
                        break;
                    }
                }

                /*
                 * 11.
                 *
                 * Aquí decidimos si el alumno:
                 *
                 * ENTREGÓ
                 *
                 * o
                 *
                 * ESTÁ PENDIENTE.
                 */
                StatusResponse status;

                if (foundSubmission != null) {

                    /*
                     * Existe una Submission.
                     *
                     * Por lo tanto:
                     *
                     * submitted = true
                     */
                    status = StatusResponse.submitted(
                            foundSubmission
                    );

                } else {

                    /*
                     * NO existe una Submission.
                     *
                     * Esto NO significa que el alumno
                     * desaparezca.
                     *
                     * Significa:
                     *
                     * submitted = false
                     *
                     * Por lo tanto:
                     *
                     * PENDIENTE.
                     */
                    status = new StatusResponse(

                            // Datos del alumno
                            child.getId(),
                            child.getName(),
                            child.getCode(),

                            // Datos de la asignatura
                            activity.getSubject().getId(),
                            activity.getSubject().getName(),

                            // Datos de la actividad
                            activity.getId(),
                            activity.getTitle(),

                            // No existe Submission
                            false,

                            // No hay fecha de entrega
                            null
                    );
                }

                /*
                 * 12.
                 *
                 * Si el usuario utilizó:
                 *
                 * ?submitted=true
                 *
                 * solamente queremos entregados.
                 *
                 * Si utilizó:
                 *
                 * ?submitted=false
                 *
                 * solamente queremos pendientes.
                 */
                if (submitted != null
                        && status.submitted() != submitted) {

                    continue;
                }

                /*
                 * 13.
                 *
                 * Agregamos el estado construido
                 * a nuestra lista final.
                 */
                result.add(status);
            }
        }

        /*
         * 14.
         *
         * Devolvemos toda la información
         * al Controller.
         */
        return result;
    }

    /**
     * Obtiene el estado de todos los alumnos
     * para una actividad específica.
     *
     * Endpoint que posteriormente utilizará:
     *
     * GET /api/activities/{id}/status
     *
     * @param activityId ID de la actividad
     * @return estado de todos los alumnos
     */
    @Transactional(readOnly = true)
    public List<StatusResponse> findByActivityId(
            UUID activityId
    ) {

        /*
         * Reutilizamos el método principal.
         *
         * No necesitamos crear otra lógica.
         *
         * Solamente enviamos el activityId.
         */
        return findStatus(
                null,
                activityId,
                null,
                null
        );
    }
}