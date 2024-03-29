package com.saturne.redwire.resources;

import com.saturne.redwire.entities.Formation;
import com.saturne.redwire.entities.Session;
import com.saturne.redwire.services.FormationService;
import com.saturne.redwire.services.SessionService;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/session")
public class SessionResource {

  private static final Logger log = LoggerFactory.getLogger(SessionResource.class);

  private final FormationService formationService;
  private final SessionService sessionService;

  @Autowired
  public SessionResource(SessionService sessionService, FormationService formationService) {
    this.sessionService = sessionService;
    this.formationService = formationService;
  }

  @GetMapping(name = "search.session", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Session>> getSessions(
    @RequestParam(value = "idTraining", defaultValue = "0") long idTraining,
    @RequestParam(value = "dateStart", required = false) String dateStart,
    @RequestParam(value = "dateEnd", required = false) String dateEnd,
    @RequestParam(value = "location", required = false) String location,
    @RequestParam(value = "price", required = false) String price
  ) {
    HashMap<String, Object> params = new HashMap<>();
    if (idTraining > 0) {
      params.put("idTraining", idTraining);
    }
    if (dateStart != null && this.isLocalDate(dateStart)) {
      params.put("dateStart", LocalDate.parse(dateStart, DateTimeFormatter.ISO_LOCAL_DATE));
    }
    if (dateEnd != null && this.isLocalDate(dateEnd)) {
      params.put("dateEnd", LocalDate.parse(dateEnd, DateTimeFormatter.ISO_LOCAL_DATE));
    }
    if (location != null) {
      params.put("location", location);
    }
    if (price != null/* && this.isFloat(price)*/) {
      params.put("price", Float.parseFloat(price));
    }
    return new ResponseEntity<>(sessionService.getSessions(params), HttpStatus.OK);
  }

  @GetMapping(name = "get.session", path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Session> getSessionById(@PathVariable("id") long id) {
    Session s = null;
    try {
      s = sessionService.getSession(id);
    } catch (EntityNotFoundException e) {
      throw new PersistenceException("Error: Cannot FIND SESSION.");
    }
    return new ResponseEntity<>(s, HttpStatus.OK);
  }

  /***
   * Create Session
   * @param session
   * @param idFormation
   * @return Session
   */
  @PostMapping(
    path = "/add/{idFormation}",
    name = "create.session"
    //consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.CREATED)
  public Session createSession(@RequestBody Session session, @PathVariable long idFormation) {
    Formation f = formationService.findFormationById(idFormation);
    log.trace("*******************CREATE SESSION***************************");
    log.trace("Found the training n°: " + idFormation + " => " + f);
    try {
      //System.out.println(session);
      session = sessionService.createSession(session);
      log.trace("session before update: " + session + "; " + session.getFormation());
      session.setFormation(f);
      sessionService.updateSession(session);
      log.trace("session after update: " + session + "; " + session.getFormation());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      return null;
    }

    return session;
  }

  @PutMapping(name = "update.session", path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Session> updateSession(
    @PathVariable(value = "id") long idSession,
    @RequestParam(value = "dateStart", required = false) String dateStart,
    @RequestParam(value = "dateEnd", required = false) String dateEnd,
    @RequestParam(value = "location", required = false) String location,
    @RequestParam(value = "price", defaultValue = "0") String price,
    @RequestParam(value = "idClassroom", defaultValue = "0") long idClassroom,
    @RequestParam(value = "idTrainer", defaultValue = "0") long idTrainer,
    @RequestParam(value = "evalSessions[]", defaultValue = "[]") String[] evalSessions,
    @RequestParam(value = "stagiaires[]", defaultValue = "[]") String[] stagiaires
  ) {
    Session s = null;
    try {
      s = sessionService.getSession(idSession);
    } catch (Exception e) {
      throw new EntityNotFoundException("Error: Cannot FIND Session.");
    }
    if (s != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
      if (dateStart != null) {
        LocalDate parsedDateStart = null;
        try {
          parsedDateStart = LocalDate.parse(dateStart, formatter);
        } catch (DateTimeParseException e) {
          throw new DateTimeException("Error: Invalid Date Format 'dateStart'.");
        }
        s.setDateDebut(parsedDateStart);
      }
      if (dateEnd != null) {
        LocalDate parsedDateEnd = null;
        try {
          parsedDateEnd = LocalDate.parse(dateEnd, formatter);
        } catch (DateTimeParseException e) {
          throw new DateTimeException("Error: Invalid Date Format 'dateEnd'.");
        }
        s.setDateFin(parsedDateEnd);
      }
      if (location != null) {
        s.setLieu(location);
      }
      if (this.isFloat(price) && Float.parseFloat(price) > 0.0f) {
        s.setPrix(Float.parseFloat(price));
      }
      if (idClassroom > 0) {
        //SalleService ss = new SalleService();
        //Salle classroom = ss.getSessionByIdSession(idClassroom);
        //s.setSalle(classroom);
      }
      if (idTrainer > 0) {
        //FormateurService fs = new FormateurService();
        //Formateur trainer = ss.getReferenceById(idTrainer);
        //s.setFormateur(trainer);
      }
      if (evalSessions.length > 0) {
        //EvalSessionService ess = new EvalSessionService();
        //List<EvalSession> evalSession = ess.findAllById(idEval);
        //s.setEvalSessions(evalSession);
      }
      if (stagiaires.length > 0) {
        //StagiaireService ss = new StagiaireService();
        //List<Stagiaire> trainee = ss.findAllById(idTrainee);
        //s.setStagiaires(trainee);
      }
      return new ResponseEntity<>(sessionService.updateSession(s), HttpStatus.OK);
    }
    return null;
  }

  @DeleteMapping(name = "delete.session", path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSessionById(@PathVariable("id") long id) {
    try {
      Session s = sessionService.getSession(id);
      log.trace("session: " + s);
      sessionService.deleteSession(s.getIdSession());
      log.trace("session deleted");
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

  private boolean isFloat(String nbStr) {
    try {
      Float.parseFloat(nbStr);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  private boolean isLocalDate(String dateStr) {
    try {
      LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }
}
