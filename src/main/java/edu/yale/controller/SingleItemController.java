package edu.yale.controller;

import edu.yale.domain.Pager;
import edu.yale.domain.Person;
import edu.yale.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class SingleItemController {

    private PersonService personService;

    @Autowired
    public SingleItemController(PersonService studentService) {
        this.personService = studentService;
    }

    /*
        The request looks something like:
        http://localhost:8080/singleitem?title=&fullName=Bard&alias=&nations=&states=&cities=
     */
    @RequestMapping(value = "/singleitem", method = RequestMethod.GET)
    public String greetingForm(final Model model,
                               @RequestParam(value = "id", required = false) Long id,
                               @RequestParam(value="fullName", required = false)  String fullName,
                               @RequestParam(value="title", required = false) String title,
                               @RequestParam(value="alias", required = false) String alias,
                               @RequestParam(value="cities", required = false) String cities,
                               @RequestParam(value="states", required = false) String states,
                               @RequestParam(value="nations", required = false) String nations,
                               @RequestParam(value="fullNameOption", required = false)  String fullNameOption,
                               @RequestParam(value="titleOption", required = false) String titleOption,
                               @RequestParam(value="aliasOption", required = false) String aliasOption,
                               @RequestParam(value="citiesOption", required = false) String citiesOption,
                               @RequestParam(value="statesOption", required = false) String statesOption,
                               @RequestParam(value="nationsOption", required = false) String nationsOption) {

        Person person = new Person();

        System.out.println("PRocessing");

        if (id != null) {
            Page<Person> persons = personService.findByPersonId(id, new PageRequest(0, 1));
            person = persons.iterator().next();
            model.addAttribute("person", person);
            return "singleitem";

        }

        System.out.println("AND title option:" + titleOption);
        System.out.println("AND name option:" + fullNameOption);


        // Build search options
        Map<SearchFields, String> fields = populateMap(title, fullName, alias, cities, states, nations);

/*        Map<SearchFields, LogicOperator> fieldOptions = populateMap2(fullNameOption, titleOption, aliasOption,
                citiesOption, statesOption);*/

        //TODO add not queries

        if (singleField(fields)) { // only need to take care of 'NOT'
            System.out.println("Output field");
            if (fields.containsKey(SearchFields.FullName)) {
                if (fullNameOption.equals(LogicOperator.NOT.name())) {

                } else { //AND
                    Page<Person> persons = personService.findByFullname(fullName, new PageRequest(0, 1));
                    person = persons.iterator().next();
                    System.out.println(person);

                }
            } else if (fields.containsKey(SearchFields.Title)) {
                if (titleOption.equals(LogicOperator.NOT.name())) {

                } else {
                    Page<Person> persons = personService.findByTitle(title, new PageRequest(0, 1));
                    person = persons.iterator().next();
                }
            } else if (fields.containsKey(SearchFields.Alias)) {
                if (aliasOption.equals(LogicOperator.NOT.name())) {

                } else {
                    Page<Person> persons = personService.findByAlias(alias, new PageRequest(0, 1));
                    person = persons.iterator().next();
                }
            } else if (fields.containsKey(SearchFields.City)) {
                if (citiesOption.equals(LogicOperator.NOT.name())) {

                } else {
                    Page<Person> persons = personService.findByCities(cities, new PageRequest(0, 1));
                    person = persons.iterator().next();
                }
            } else if (fields.containsKey(SearchFields.State)) {
                if (statesOption.equals(LogicOperator.NOT.name())) {

                } else {
                    Page<Person> persons = personService.findByStates(states, new PageRequest(0, 1));
                    person = persons.iterator().next();
                }
            } else if (fields.containsKey(SearchFields.Nation)) {
                if (nationsOption.equals(LogicOperator.NOT.name())) {

                } else {
                    Page<Person> persons = personService.findByNations(nations, new PageRequest(0, 1));
                    person = persons.iterator().next();
                }
            }

        } else {
            System.out.println("Multiple fields");
        }

        String[] formOptions = new String[] {titleOption, fullNameOption, aliasOption, citiesOption, statesOption, nationsOption};
        String[] formOptions_no_title = new String[] {fullNameOption, aliasOption, citiesOption, statesOption, nationsOption};
        String[] formOptions_no_fullname = new String[] {titleOption, aliasOption, citiesOption, statesOption, nationsOption};
        String[] formOptions_no_alias = new String[] {titleOption, fullNameOption, citiesOption, statesOption, nationsOption};
        String[] formOptions_no_city = new String[] {titleOption, fullNameOption, aliasOption, statesOption, nationsOption};
        String[] formOptions_no_state= new String[] {titleOption, fullNameOption, aliasOption, citiesOption, nationsOption};

        String[] formOptions_no_nations = new String[] {titleOption, fullNameOption, aliasOption, citiesOption, statesOption};


        // else if multiple fields:

            // if all ANDs

            // if all ORs

            // not fullname = bard and states = france => finds all france and excludes bard

            // not fullname = bard not states = france => finds everything and excludes bard and france

            List<Person> personsCopy = new ArrayList<>();


            PageRequest pr = new PageRequest(0, 10);

            if (fields.size() == 5) {
                if (title == null) {
                    if (allAndSearch(formOptions_no_title)) {
                        personService.findByFullnameAndAliasAndNationsAndStatesAndCities(fullName, alias, nations, states, cities, pr);
                     } else if (allAndSearch(formOptions_no_title)){
                        //FIXME change to OR
                        personService.findByFullnameAndAliasAndNationsAndStatesAndCities(fullName, alias, nations, states, cities, pr);
                    }
                } else if (fullName == null) {
                    if (allAndSearch(formOptions_no_fullname)) {
                        personService.findByTitleAndAndAliasAndNationsAndStatesAndCities(title, alias, nations, states, cities, pr);
                    } else if (allAndSearch(formOptions_no_title)){
                        //FIXME change to OR
                        personService.findByTitleAndAndAliasAndNationsAndStatesAndCities(title, alias, nations, states, cities, pr);
                    }
                } else if (alias == null) {
                    if (allAndSearch(formOptions_no_alias)) {
                        personService.findByTitleAndAndFullnameAndNationsAndStatesAndCities(title, fullName, nations, states, cities, pr);
                    } else if (allAndSearch(formOptions_no_alias)){
                        //FIXME change to OR
                        personService.findByTitleAndAndFullnameAndNationsAndStatesAndCities(title, fullName, nations, states, cities, pr);
                    }
                } else if (cities == null) {
                    if (allAndSearch(formOptions_no_city)) {
                        personService.findByTitleAndFullnameAndAliasAndNationsAndStates(title, fullName, alias, nations, states, pr);
                    } else if (allAndSearch(formOptions_no_city)){
                        //FIXME change to OR
                        personService.findByTitleAndFullnameAndAliasAndNationsAndStates(title, fullName, alias, nations, states, pr);
                    }
                } else if (nations == null) {

                    if (allAndSearch(formOptions_no_nations)) {
                        personService.findByTitleAndAndFullnameAndAliasAndStatesAndCities(title, fullName, alias, states, cities, pr);
                    } else if (allOrSearch(formOptions_no_nations)){
                        //FIXME change to OR
                        personService.findByTitleAndAndFullnameAndAliasAndStatesAndCities(title, fullName, alias, states, cities, pr);
                    }

                } else if (states == null) {

                    if (allAndSearch(formOptions_no_state)) {
                        personService.findByTitleAndAndFullnameAndAliasAndNationsAndCities(title, fullName, alias, nations, cities, pr);
                    } else if (allOrSearch(formOptions_no_state)){
                        //FIXME change to OR
                        personService.findByTitleAndAndFullnameAndAliasAndNationsAndCities(title, fullName, alias, nations, cities, pr);
                    }
                }
            }

            if (fields.size() == 6) {
                if (allAndSearch(formOptions)) {
                    personService.findByTitleAndFullnameAndAliasAndNationsAndStatesAndCities(title, fullName, alias, nations, states, cities, pr);
                } else if (allOrSearch(formOptions)) {
                    personService.findByTitleAndFullnameAndAliasAndNationsAndStatesAndCities(title, fullName, alias, nations, states, cities, pr);
                }
            }

            // the dirty way (removes all pagination)

             pr = new PageRequest(0, 3700);

            // removes pagination
            if (fields.size() >= 2 &&  fields.size() <= 4) {
                Page<Person> persons = personService.findAll(pr); // or load at cache time

                System.out.println("Persons size:" + persons.getSize());

                Iterator it = persons.iterator();

                while (it.hasNext()) {

                    boolean titleMatch = false;
                    boolean fullNameMatch= false;
                    boolean aliasMatch = false;
                    boolean citiesMatch = false;
                    boolean nationsMatch = false;
                    boolean statesMatch = false;

                    final Person p = (Person) it.next();

                    //System.out.println("Considering:" + p);

                    if (!title.isEmpty()) {
                        if (!(p.getTitle() != null && p.getTitle().contains(title))) {
                            titleMatch = false;
                        } else {
                            titleMatch = true;
                            System.out.println("Found a match!" + p.getTitle() + " name:" + p.getFullName());

                        }

                    }

                    if (!fullName.isEmpty()) {
                        if (!(p.getFullName() != null && p.getFullName().contains(fullName))) {
                            fullNameMatch = false;
                            //System.out.println("Not a full match:" + p.getFullName());
                        } else {
                            fullNameMatch = true;
                            System.out.println("Found a match!" + p.getFullName());
                        }
                    } else {
                        System.out.println("SKipped full name match");
                    }

                    if (!alias.isEmpty()) {
                        if (!(p.getAlias() != null && p.getAlias().equals(alias))) {
                            aliasMatch = false;
                        } else {
                            aliasMatch = true;
                        }
                    }

                    if (!states.isEmpty()) {
                        if (!(p.getStates() != null && p.getStates().equals(states))) {
                            statesMatch = false;
                        } else {
                            statesMatch = true;
                        }
                    }

                    if (!cities.isEmpty()) {
                        if (!(p.getCities() != null && p.getCities().equals(cities))) {
                            citiesMatch = false;
                        } else {
                            citiesMatch = true;
                        }

                    }

                    if (!nations.isEmpty()) {
                        if (!(p.getNations() != null && p.getNations().equals(nations))) {
                            nationsMatch = false;
                        } else {
                            nationsMatch = true;
                        }
                    }

                    // 2 fields



                    if (title != null && fullName != null && titleOption.equals("AND") && fullNameOption.equals("AND")) {
                        if (titleMatch && fullNameMatch) {
                            System.out.println("Doing a title and full name match search");
                            personsCopy.add(p);
                        }

                    }

                    if (allAndSearch(titleOption, aliasOption)) {
                        if (titleMatch && aliasMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(titleOption, statesOption)) {
                        if (titleMatch && statesMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(titleOption, citiesOption)) {
                        if (titleMatch && citiesMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(titleOption, nationsOption)) {
                        if (titleMatch && nationsMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(fullNameOption, aliasOption)) {
                        if (fullNameMatch && aliasMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(fullNameOption, statesOption)) {
                        if (fullNameMatch && statesMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(fullNameOption, citiesOption)) {
                        if (fullNameMatch && citiesMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(aliasOption, nationsOption)) {
                        if (aliasMatch && nationsMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(aliasOption, statesOption)) {
                        if (aliasMatch && statesMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(aliasOption, citiesOption)) {
                        if (aliasMatch && citiesMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(aliasOption, nationsOption)) {
                        if (aliasMatch && nationsMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(statesOption, nationsOption)) {
                        if (statesMatch && nationsMatch) {
                            personsCopy.add(p);
                        }
                    }

                    if (allAndSearch(statesOption, citiesOption)) {
                        if (statesMatch && citiesMatch) {
                            personsCopy.add(p);
                        }
                    }


                    // 3 fields

                    if (allAndSearch(titleOption, fullNameOption, aliasOption)) {
                        if (titleMatch && fullNameMatch && aliasMatch) {
                            personsCopy.add(p);
                        }
                    }

                    // TODO add more here

                }

                // System.out.println("Out of loop");
                System.out.println("The list is:" + personsCopy.toString());
            }

        //model.addAttribute("persons", personsCopy);

        //System.out.println("The list is:" + personsCopy.toString());

        final Page<Person> page = new PageImpl<>(personsCopy);

        System.out.println(page.iterator().next());

        final Pager pager = new Pager(page.getTotalPages(), page.getNumber(), 5);
        model.addAttribute("persons", page);
        model.addAttribute("selectedPageSize",100);
        model.addAttribute("pageSizes", 10);
        model.addAttribute("pager", pager);
        return "persons";

    }

    private boolean allAndSearch(String titleOption, String fullNameOption, String aliasOption, String cityOption, String stateOption, String nationOption) {
        if (isAnd(titleOption) && isAnd(fullNameOption)
                && isAnd(aliasOption) && isAnd(cityOption) && isAnd(stateOption) && isAnd(nationOption)) {
            return true;
        }
        return false;
    }

    private boolean allAndSearch(String... options) {

        boolean result = true;

        for (String s : options) {
            if (!isAnd(s)) {
                result = false;
                break;
            }
        }



        return result;
    }


    private boolean allOrSearch(String... options) {

        boolean result = true;

        for (String s : options) {
            if (!isOr(s)) {
                result = false;
                break;
            }
        }

        return result;
    }

    private boolean allOrSearch(String titleOption, String fullNameOption, String aliasOption, String cityOption, String stateOption, String nationOption) {
        if (isOr(titleOption) && isOr(fullNameOption)
                && isOr(aliasOption) && isOr(cityOption) && isOr(stateOption) && isOr(nationOption)) {
            return true;
        }
        return false;
    }

    private boolean isAnd(String string) {
        return string != null && string.equals("AND");
    }

    private boolean isOr(String string) {
        return string.equals("OR");
    }

    private boolean isNot(String string) {
        return string.equals("NOT");
    }


    private boolean containsField(final Map<SearchFields, String> fields, final SearchFields request) {
        return fields.containsKey(request);
    }

    private boolean singleField(final Map<SearchFields, String> fields) {
        System.out.println("Field size" + fields.size());
        return fields.size() == 1;
    }

    // populate form objects
    // sigh
    private Map<SearchFields, String> populateMap(String title, String fullName, String alias, String cities, String states, String nations) {
        final Map<SearchFields, String> fields = new HashMap<>();

        if (!fullName.isEmpty()) {
            fields.put(SearchFields.FullName, fullName);
        }
        if (!title.isEmpty()) {
            fields.put(SearchFields.Title, title);
            System.out.println(title);

        }

        if (!alias.isEmpty()) {
            fields.put(SearchFields.Alias, alias);

        }

        if (!cities.isEmpty()) {
            fields.put(SearchFields.City, cities);

        }

        if (!states.isEmpty()) {
            fields.put(SearchFields.State, states);

        }

        if (!nations.isEmpty()) {
            fields.put(SearchFields.Nation, nations);

        }
        return fields;
    }

    private enum SearchFields {
        FullName,
        Title,
        Alias,
        City,
        State,
        Nation
    }

    private enum LogicOperator {
        AND,
        OR,
        NOT
    }
}

