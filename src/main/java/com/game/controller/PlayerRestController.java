package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/rest/players")
public class PlayerRestController {

    @Autowired
    PlayerService playerService;

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long playerId) {
        if (playerId == null || playerId < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Player> player = this.playerService.findById(playerId);

        if (!player.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(player.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@RequestBody @Validated Player player) {
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!player.isValidPlayer()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        player.calculateLevel();
        player.calculateUntilNextLevel();
        this.playerService.save(player);

        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> updatePlayer(@RequestBody @Validated Player player, @PathVariable("id") Long playerId) {
        if (playerId == null || playerId < 1 || player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Player> playerById = this.playerService.findById(playerId);

        if (!playerById.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (playerById.get().update(player)) {
            this.playerService.save(playerById.get());
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(playerById.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") Long playerId) {
        if (playerId == null || playerId < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Player> player = this.playerService.findById(playerId);

        if (!player.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        this.playerService.delete(playerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Player>> getAllPlayers(@RequestParam(value = "order", required = false) PlayerOrder order,
                                                      @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                      @RequestParam(value = "name", required = false) String name,
                                                      @RequestParam(value = "title", required = false) String title,
                                                      @RequestParam(value = "race", required = false) Race race,
                                                      @RequestParam(value = "profession", required = false) Profession profession,
                                                      @RequestParam(value = "after", required = false) Long after,
                                                      @RequestParam(value = "before", required = false) Long before,
                                                      @RequestParam(value = "banned", required = false) Boolean banned,
                                                      @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                      @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                      @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                      @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        List<Player> players = this.playerService.findAll();
        Stream<Player> stream = players.stream();
        stream = name == null ? stream : stream.filter(player -> player.getName().contains(name));
        stream = title == null ? stream : stream.filter(player -> player.getTitle().contains(title));
        stream = race == null ? stream : stream.filter(player -> player.getRace() == race);
        stream = profession == null ? stream : stream.filter(player -> player.getProfession() == profession);
        stream = after == null ? stream : stream.filter(player -> player.getBirthday().getTime() >= after);
        stream = before == null ? stream : stream.filter(player -> player.getBirthday().getTime() <= before);
        stream = banned == null ? stream : stream.filter(player -> player.getBanned() == banned);
        stream = minExperience == null ? stream : stream.filter(player -> player.getExperience() >= minExperience);
        stream = maxExperience == null ? stream : stream.filter(player -> player.getExperience() <= maxExperience);
        stream = minLevel == null ? stream : stream.filter(player -> player.getLevel() >= minLevel);
        stream = maxLevel == null ? stream : stream.filter(player -> player.getLevel() <= maxLevel);

        order = order == null ? PlayerOrder.ID : order;
        pageNumber = pageNumber == null ? 0 : pageNumber;
        pageSize = pageSize == null ? 3 : pageSize;

        PagedListHolder<Player> page = new PagedListHolder<>(stream.collect(Collectors.toList()));
        page.setSort(new MutableSortDefinition(order.getFieldName(), false, true));
        page.resort();
        page.setPage(pageNumber);
        page.setPageSize(pageSize);

        return new ResponseEntity<>(page.getPageList(), HttpStatus.OK);
    }

    @RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getCountPlayers(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "title", required = false) String title,
                                                   @RequestParam(value = "race", required = false) Race race,
                                                   @RequestParam(value = "profession", required = false) Profession profession,
                                                   @RequestParam(value = "after", required = false) Long after,
                                                   @RequestParam(value = "before", required = false) Long before,
                                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        List<Player> players = this.playerService.findAll();
        Stream<Player> stream = players.stream();
        stream = name == null ? stream : stream.filter(player -> player.getName().contains(name));
        stream = title == null ? stream : stream.filter(player -> player.getTitle().contains(title));
        stream = race == null ? stream : stream.filter(player -> player.getRace() == race);
        stream = profession == null ? stream : stream.filter(player -> player.getProfession() == profession);
        stream = after == null ? stream : stream.filter(player -> player.getBirthday().getTime() >= after);
        stream = before == null ? stream : stream.filter(player -> player.getBirthday().getTime() <= before);
        stream = banned == null ? stream : stream.filter(player -> player.getBanned() == banned);
        stream = minExperience == null ? stream : stream.filter(player -> player.getExperience() >= minExperience);
        stream = maxExperience == null ? stream : stream.filter(player -> player.getExperience() <= maxExperience);
        stream = minLevel == null ? stream : stream.filter(player -> player.getLevel() >= minLevel);
        stream = maxLevel == null ? stream : stream.filter(player -> player.getLevel() <= maxLevel);

        long count = stream.count();
        return new ResponseEntity<>((int) count, HttpStatus.OK);
    }
}
