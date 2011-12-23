package com.chrishoekstra.trello.model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.chrishoekstra.trello.vo.AllBoardsResultVO;
import com.chrishoekstra.trello.vo.BoardResultVO;
import com.chrishoekstra.trello.vo.CardVO;
import com.chrishoekstra.trello.vo.MemberVO;
import com.chrishoekstra.trello.vo.NotificationVO;
import com.chrishoekstra.trello.vo.NotificationsResultVO;

public class TrelloModel {
    
    // Singleton stuff
    private static TrelloModel model;
    public  static TrelloModel getInstance() {
        if (model == null) {
            model = new TrelloModel();
            model.mCardNotifications = new HashMap<String, Integer>();
        }
        return model;
    }
    
    
    // Listener Declaration
    public interface OnLoginCompleteListener {
        void onLoginCompleteEvent(TrelloModel model, boolean successful);
    }

    public interface OnBoardReceivedListener {
        void onBoardReceveidEvent(TrelloModel model, BoardResultVO result);
    }

    public interface OnNotificationsReceivedListener {
        void onNotificationsReceivedEvent(TrelloModel model, NotificationsResultVO result);
    }

    public interface OnCardAddedListener {
        void onCardAddedEvent(TrelloModel model, CardVO card);
    }
    
    
    // Listener Arrays
    private final ConcurrentLinkedQueue<OnLoginCompleteListener> onLoginCompleteListeners                 = new ConcurrentLinkedQueue<OnLoginCompleteListener>();
    private final ConcurrentLinkedQueue<OnBoardReceivedListener> onBoardReceivedListeners                 = new ConcurrentLinkedQueue<OnBoardReceivedListener>();
    private final ConcurrentLinkedQueue<OnNotificationsReceivedListener> onNotificationsReceivedListeners = new ConcurrentLinkedQueue<OnNotificationsReceivedListener>();
    private final ConcurrentLinkedQueue<OnCardAddedListener> onCardAddedListeners                         = new ConcurrentLinkedQueue<OnCardAddedListener>();
    
    
    // Alert Listeners
    private final void alertOnLoginCompleteListeners(boolean successful) {
        for (final OnLoginCompleteListener listener : onLoginCompleteListeners)
            listener.onLoginCompleteEvent(this, successful);
    }
    
    private final void alertOnBoardReceivedListeners(BoardResultVO result) {
        for (final OnBoardReceivedListener listener : onBoardReceivedListeners)
            listener.onBoardReceveidEvent(this, result);
    }

    private final void alertOnNotificationsReceivedListeners(NotificationsResultVO result) {
        for (final OnNotificationsReceivedListener listener : onNotificationsReceivedListeners)
            listener.onNotificationsReceivedEvent(this, result);
    }
    
    private final void alertOnCardAddedListeners(CardVO card) {
        for (final OnCardAddedListener listener : onCardAddedListeners)
            listener.onCardAddedEvent(this, card);
    }
    

    // Add Listener
    public final void addListener(OnLoginCompleteListener listener) {
        synchronized (onLoginCompleteListeners) {
            onLoginCompleteListeners.add(listener);
        }
    }

    public final void addListener(OnBoardReceivedListener listener) {
        synchronized (onBoardReceivedListeners) {
            onBoardReceivedListeners.add(listener);
        }
    }

    public final void addListener(OnNotificationsReceivedListener listener) {
        synchronized (onNotificationsReceivedListeners) {
            onNotificationsReceivedListeners.add(listener);
        }
    }

    public final void addListener(OnCardAddedListener listener) {
        synchronized (onCardAddedListeners) {
            onCardAddedListeners.add(listener);
        }
    }

    
    // Remove Listener
    public final void removeListener(OnLoginCompleteListener listener) {
        synchronized (onLoginCompleteListeners) {
            onLoginCompleteListeners.remove(listener);
        }
    }

    public final void removeListener(OnBoardReceivedListener listener) {
        synchronized (onBoardReceivedListeners) {
            onBoardReceivedListeners.remove(listener);
        }
    }

    public final void removeListener(OnNotificationsReceivedListener listener) {
        synchronized (onNotificationsReceivedListeners) {
            onNotificationsReceivedListeners.remove(listener);
        }
    }

    public final void removeListener(OnCardAddedListener listener) {
        synchronized (onCardAddedListeners) {
            onCardAddedListeners.remove(listener);
        }
    }
    
    
    // Methods
    public AllBoardsResultVO getAllBoardsResult() {
        return mAllBoardsResult;
    }
    
    public void setAllBoardsResult(AllBoardsResultVO value) {
        mAllBoardsResult = value;
        
        for (NotificationVO notification : value.notifications) {
            if (notification.isUnread && (notification.data.card != null)) {
                int count = 1;
                
                if (mCardNotifications.containsKey(notification.data.card.id)) {
                    count += mCardNotifications.get(notification.data.card.id);
                }
                
                mCardNotifications.put(notification.data.card.id, count);
            }
        }
    }
    
    public BoardResultVO getCurrentBoard() {
        return mCurrentBoard;
    }
    
    public void setCurrentBoard(BoardResultVO result) {
        mCurrentBoard = result;
    }
    
    public void boardReceived(BoardResultVO result) {
        alertOnBoardReceivedListeners(result);
    }

    public void notificationsReceived(NotificationsResultVO result) {
        alertOnNotificationsReceivedListeners(result);
    }
    
    public void cardAdded(CardVO card) {
        alertOnCardAddedListeners(card);
    }

    public void setUser(MemberVO user) {
        mUser = user;
    }
    
    public MemberVO getUser() {
        return mUser;
    }
    
    public int getNotificationCount(String idCard) {
        if (mCardNotifications.containsKey(idCard)) {
            return mCardNotifications.get(idCard);
        } else {
            return 0;
        }
    }
    
    // Variables
    private AllBoardsResultVO mAllBoardsResult;
    private BoardResultVO mCurrentBoard;
    private MemberVO mUser;
    private HashMap<String, Integer> mCardNotifications;
    
    // Model functions
    public void loginComplete(boolean successful) {
        alertOnLoginCompleteListeners(successful);
    }
}
