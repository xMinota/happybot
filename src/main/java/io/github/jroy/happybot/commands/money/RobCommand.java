package io.github.jroy.happybot.commands.money;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.sql.UserToken;
import net.dv8tion.jda.core.entities.Member;

import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class RobCommand extends CommandBase {

    private final static int FINE = 300;

    private SQLManager sqlManager;

    private final HashMap<String, RobToken> robTokens = new HashMap<>();

    public RobCommand(SQLManager sqlManager) {
        super("rob", "<user>", "Robs a user for their money.", CommandCategory.FUN);
        this.sqlManager = sqlManager;
        setCooldown(30, ChronoUnit.MINUTES);
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        try {
            if (!e.containsMention()) {
                e.reply(invalid);
                removeFromCooldown(e.getMember());
                return;
            }

            String userId = e.getMember().getUser().getId();
            Member targetMember = e.getMentionedMember();
            if (!sqlManager.isActiveUserH(userId) || !sqlManager.isActiveUserH(targetMember.getUser().getId())) {
                e.reply("Both you and the person you are attempting to rob must have a money account!");
                removeFromCooldown(e.getMember());
                return;
            }
            if (userId.equalsIgnoreCase(targetMember.getUser().getId())) {
                e.reply("Why would you rob yourself?");
                removeFromCooldown(e.getMember());
                return;
            }

            if (!robTokens.containsKey(userId)) {
                robTokens.put(userId, new RobToken());
            }
            RobToken token = robTokens.get(userId);
            if (!token.canRob(targetMember.getUser().getId())) {
                e.replyError("You may only rob a user once a day!");
                removeFromCooldown(e.getMember());
                return;
            }

            UserToken userToken = sqlManager.getUser(userId);
            UserToken targetToken = sqlManager.getUser(targetMember.getUser().getId());

            if (targetToken.getCoins() < 1000) {
                e.reply("This person has under 1000 coins, give them a break!");
                removeFromCooldown(e.getMember());
                return;
            }

            int robAmount = ThreadLocalRandom.current().nextInt(100, 500);
            if (targetToken.getCoins() < robAmount) {
                e.reply("The person you are trying to steal from does not have any money to steal! You got caught in the act!\n" +
                    "    -" + FINE + " coins.");
                token.registerRob(targetMember.getUser().getId());
                userToken.takeCoins(Math.min(FINE, userToken.getCoins()));
                return;
            }

            double chance = Math.random();
            if (chance < 0.5) {
                targetToken.takeCoins(robAmount);
                userToken.addCoins(robAmount);
                token.registerRob(targetMember.getUser().getId());
                e.reply("Hey " + targetMember.getAsMention() + ", you just got robbed by " + e.getMember().getAsMention() + " for " + robAmount + " coins!");
            } else {
                e.reply(e.getMember().getAsMention() + ", the feds caught you in the act you thief.\n" +
                    "    -" + FINE + " coins as fine.");
                token.registerRob(targetMember.getUser().getId());
                userToken.takeCoins(Math.min(FINE, userToken.getCoins()));
            }
        } catch (SQLException e1) {
            e.reply("Oof Error: " + e1.getMessage());
            removeFromCooldown(e.getMember());

        }
    }
}
