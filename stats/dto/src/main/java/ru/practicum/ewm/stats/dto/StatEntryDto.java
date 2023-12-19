package ru.practicum.ewm.stats.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@ToString
public class StatEntryDto {
    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;

    @Pattern(regexp =
      "^\\d{4}-((0\\d)|(1[012]))-((0[1-9])|([12][0-9])|(3[0-2])) (([01][0-9])|(2[0-3]))(:([0-5][0-9])){2}$")
    private String timestamp;
}

/*  IPv4 regular expression template
        (25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.
        (25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)

    IPv6 regular expression template
        ((^|:)([0-9a-fA-F]{0,4})){1,8}
 */