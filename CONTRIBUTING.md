
# You can help in different ways!

## Users

You can help with testing, bug reports, false positive reports, and bypass reports. Use Github Issues or our Discord server (https://discord.gg/63JGnay).

## Developers

If you know Java you can help us fix errors, bypasses, and false positives, as well as add new config options and organise the code.

This is an open-source repository and we are open to PRs.

### Requirements

You need to know Java.

Ideally, you'll know git and maven. If you don't, message us on the discord and we can help you with some things.

You'll want to install the following if you haven't already:

* Git
* Maven
* JDK 11 or greater

### Creating Checks

There are 2 ways you can specify checks.

**The Simple Way**

Most checks will follow this approach.

1. The traditional way. This approach is the easiest to change existing checks to.

1. Extend `Check` or `ListeningCheck` depending on whether the check needs to listen to an event.
2. Declare a constructor with the same signature as the superconstructor of the class you extended.
3. Declare a `public static final CheckInfo checkInfo` field. Use one of the static factory methods in `CheckInfos` to get an instance.

The check info should match which base class was extended. ListeningCheckInfo corresponds to ListeningCheck, for example. Depending on the check extended, you will have to implement some methods. Note that if your check info contains a periodic async task, you must override `checkAsyncPeriodic()`. Use `@Override` for readability purposes.

Now your check may look like this:

```java
public class YourCheck extends Check {

  public static final CheckInfo checkInfo = CheckInfos.forEventWithAsyncPeriodic(PlayerInteractEvent.class, Duration.ofSeconds(1L));

  public YourClass(CheckFactory<?> factory, NessPlayer nessPlayer) {
    super(factory, nessPlayer);
  }
  
}
```

**With a specific check factory**

You most likely won't need to use this until you are more involved with NESS and/or start making lots of checks.

1. Create a factory class with the same name as the check, extending `ListeningCheckFactory`, `CheckFactory`, or `BaseCheckFactory`.
    * Note that `BaseCheckFactory` should only be used in  certain cases, for "fake checks" - checks which are not related to any specific player. More on this below
    * Declare a constructor with 1 parameter, namely CheckManager. Other details about the check should be passed to the super constructor, including check instantiator and check name.
    * Here is an example using the AutoClick check:

```java
public class AutoClickFactory extends ListeningCheckFactory<AutoClick> {

  public AutoClickFactory(CheckManager manager) {
    super(AutoClick::new, "AutoClick", manager,
      CheckInfos.forEventWithAsyncPeriodic(PlayerInteractEvent.class, Duration.ofSeconds(1L)));
  }

}
```

About BaseCheckFactory - Use of BaseCheckFactory, BaseCheckInfo, and BaseCheck directly is for "fake checks" - those which are not maintained per-player. (If per-player checks are desired, CheckFactory or ListeningCheckFactory should be used)

When implementing `BaseCheckFactory#newCheck(NessPlayer)`, the returned check is insignificant. It can be null or the same instance of an object. This method only exists to inform `BaseCheckFactory`s when a NessPlayer is added to the server. `removeCheck(NessPlayer)` is used to notify removal of a NessPlayer.

## Choosing the right CheckInfo

CheckInfos.forEvent(PlayerInteractEvent.class) - your check will be called every time the PlayerInteractEvent is fired. You must extend ListeningCheck<PlayerInteractEvent> and override `checkEvent`.

CheckInfos.asyncPeriodic(Duration.ofSeconds(1L)) - your check's `checkAsyncPeriodic` method will be called every second. This method needs to be overridden.

CheckInfos.forEventWithAsyncPeriodic(PlayerInteractEvent.class, Duration.ofSeconds(1L)) - both `checkEvent` and `checkAsyncPeriodic` need to be overridden.

**A Note of Caution on Listening Checks**

Checks are instantiated per player. When you receive an event, you should ensure the event only concerns your player.

If the event has a 'getPlayer()' method, it will be automatically filtered and you don't have to worry about this. In all other cases, you need to ensure you don't listen to unrelated events. For example, with the EntityDamageByEntityEvent:

```
public class Killaura extends AbstractCheck<EntityDamageByEntityEvent> {

	public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos.forEvent(EntityDamageByEntityEvent.class);

	public Killaura(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
  }

	@Override
	protected void checkEvent(EntityDamageByEntityEvent e) {
		if (player().isNot(e.getDamager())) {
			return;
    }
    // continue check here
  }
```

There are helper methods to make this easier, such as player() which returns the NessPlayer your check is running for.

### Other helper methods

**Methods in BaseCheck/Check/ListeningCheck**

manager() - returns CheckManager
ness() - returns the main plugin, NESSAnticheat
player() - returns the NessPlayer associated with the check. Not applicable for BaseCheck

runTaskLater(Runnable, Duration) - runs a delayed synchronous task
durationOfTicks(int) - converts an amount of ticks into a Duration

getFactory() - returns the factory which created the check

**Methods in NessPlayer**

is(Entity) - checks that the Entity is a Player and is the same player as the NessPlayer

isNot(Entity) - inverse operation of is(Entity)

getUUID() - thread safe getter for the player's UUID
