package io.github.jroy.happybot.util;

import net.dv8tion.jda.core.entities.TextChannel;

public enum Channels {

  RULES("264966606528970752"),
  WELCOME("237363812842340363"),
  LIVE("294588669682122752"),
  UPDATES("237364012940001280"),
  BOT_META("360544824434098188"),
  TWITTER("362333614580432896"),
  LEADERBOARD("464502770574163982"),
  RANDOM("337920467450986497"),
  TRUE_FALSE_GAME("474616686201536523"),
  GAMBLE("384102400308412416"),
  PATREON_BOYS("300089332616921088"),
  MUSIC_REQUEST("264966756831854592"),
  STARRED_MESSAGES("369214529847951361"),
  FANART("337689640888827905"),
  WELCOME_SUBMIT("362021830766100480"),
  ARCHIVED_RANDOM("294926978882994186"),
  LOG("318456047993880577"),
  REPORT("352157503124930570"),
  STAFF_ANNOUNCEMENTS("366437355579768852"),
  STAFF_DISCUSSION("294934556904259585"),
  TESTING("267145626469400586"),
  SUPER_ADMIN("359135263638814721");

  private String channelId;

  Channels(String channelId) {
    this.channelId = channelId;
  }

  public String getId() {
    return channelId;
  }

  public TextChannel getChannel() {
    return C.getGuild().getTextChannelById(channelId);
  }
}
