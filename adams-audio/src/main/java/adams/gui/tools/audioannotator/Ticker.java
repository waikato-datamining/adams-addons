/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Ticker.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.audioannotator;

import adams.gui.audio.AudioPlaybackPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Provides tick events to listeners at various times
 *
 * @author sjb90
 */
public class Ticker {

  /** A scheduler to execute our command */
  protected ScheduledExecutorService m_Scheduler;

  /** A list of handlers for scheduled ticks */
  protected List<ScheduledFuture<?>> m_Handlers;

  /** a set of the runnable's */
  protected HashMap<Long, Tick> m_Ticks;

  /** the media player to get timestamps from */
  protected AudioPlaybackPanel m_AudioPlayer;

  /**
   * Constructer for the ticker class.
   *
   * @param player the playback panel
   */
  public Ticker(AudioPlaybackPanel player) {
    m_Scheduler   = Executors.newScheduledThreadPool(1);
    m_Handlers    = new ArrayList<>();
    m_Ticks 	  = new HashMap<>();
    m_AudioPlayer = player;
  }

  /**
   * adds a listener to the ticker
   * @param tickListener the listener to add.
   */
  public void addListener(TickListener tickListener) {
    Long key = tickListener.getInterval();
    if (m_Ticks.containsKey(key)) {
      m_Ticks.get(key).addListener(tickListener);
    }
    else {
      Tick newTick = new Tick();
      newTick.addListener(tickListener);
      m_Ticks.put(key, newTick);
      m_Handlers.add(m_Scheduler.scheduleAtFixedRate(newTick, 0, key, TimeUnit.MILLISECONDS));
    }
  }

  /**
   * Shuts down all threads and removes them and the ticks from the ticker
   */
  public void removeAll() {

    for(ScheduledFuture<?> future : m_Handlers) {
      future.cancel(false);
    }
    m_Handlers = new ArrayList<>();

    m_Ticks = new HashMap<>();
  }

  /**
   * private class which represents an event that happens at a given interval
   */
  private class Tick implements Runnable {

    private List<TickListener> m_Listeners;

    Tick() {
      m_Listeners = new ArrayList<>();
    }

    @Override
    public void run() {
      notifyTickListeners();
    }

    private void notifyTickListeners() {
      long msec = m_AudioPlayer.getTimestamp();
      if (msec == -1 || !m_AudioPlayer.isPlaying())
	return;
      Date timeStamp = new Date(msec);
      TickEvent tickEvent = new TickEvent(timeStamp);
      for (TickListener listener : m_Listeners) {
	listener.tickHappened(tickEvent);
      }
    }

    public void addListener(TickListener tickListener) {
      m_Listeners.add(tickListener);
    }
  }
}
