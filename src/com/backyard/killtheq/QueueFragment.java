package com.backyard.killtheq;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backyard.killtheq.asynctask.AsyncTaskListener;
import com.backyard.killtheq.asynctask.DequeueAsyncTask;
import com.backyard.killtheq.asynctask.EnqueueAsyncTask;
import com.backyard.killtheq.asynctask.GetQueueAsyncTask;
import com.backyard.killtheq.boradcastReceiver.BroadcastReceiverListener;
import com.backyard.killtheq.boradcastReceiver.TimeChangedReceiver;
import com.backyard.killtheq.model.Enqueue;
import com.backyard.killtheq.widgets.HoloCircularProgressBar;

public class QueueFragment extends Fragment{
	
    private static final String ARG_SECTION_NUMBER = "section_number";
    
    private HoloCircularProgressBar progressbar;
    private TextView progressStatus;
    private TextView togo;
    private Button enterQueue;
    private static int remainingPerson;
    private static int queueNumber;
    private EnqueueAsyncTask enqueueTask;
    private GetQueueAsyncTask getQueueTask;
    private AsyncTaskListener<Enqueue> enqueueListener;
    private AsyncTaskListener<Integer> getQueueListener;
    private ObjectAnimator progressBarAnimator;
    private float singleStep;
    private float currentStep;
    private TimeChangedReceiver receiver;
    private BroadcastReceiverListener<Void> receiverListener;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static QueueFragment newInstance(int sectionNumber) {
    	QueueFragment fragment = new QueueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    
    public QueueFragment(){
    	
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		if(remainingPerson < 1){
			return;
		}
		
		startCheckingQueue();   
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        progressbar = (HoloCircularProgressBar)rootView.findViewById(R.id.cpbQueue);
        progressStatus = (TextView)rootView.findViewById(R.id.tvQueueInfo);
        togo = (TextView)rootView.findViewById(R.id.tvTogo);
        enterQueue = (Button)rootView.findViewById(R.id.btnEnter);
        
        enterQueue.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				enterQueue.setVisibility(View.GONE);
				initiateEnqueueAsyncTaskListner();
				progressStatus.setOnClickListener(null);
				enqueueTask = new EnqueueAsyncTask(enqueueListener);
				enqueueTask.execute();
			}
		});
        
        return rootView;
    }

    @Override
	public void onResume() {
		super.onResume();
		
		toggleQueueState(remainingPerson > 0);
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.queue, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.action_leave_queue:
				new DequeueAsyncTask(null).execute(queueNumber);
	            currentStep = 0;
	            singleStep = 0;
	            queueNumber = 0;
	            remainingPerson = -1;
	            toggleQueueState(false);
	            return true;
			case R.id.action_create_notification:
				createNotification();
				return true;
				
			case R.id.action_refresh:
				
				if(getQueueListener == null){
					getQueueListener = new AsyncTaskListener<Integer>(){

						@Override
						public void onPostExecuteCallBack(Integer result) {
							if(result < 0){
								return;
							}
							
							updateProgress(result);
						}

						@Override
						public void onPreExecuteCallBack() {
							
						}
					};
				}
				
				getQueueTask = new GetQueueAsyncTask(getQueueListener);
				getQueueTask.execute(queueNumber);
				return true;
		}
        
		return super.onOptionsItemSelected(item);
	}

	private void initiateEnqueueAsyncTaskListner(){
		enqueueListener = new AsyncTaskListener<Enqueue>(){

			@Override
			public void onPostExecuteCallBack(Enqueue result) {
				progressBarAnimator.cancel();
				animate(progressbar, new AnimatorListener(){

					@Override
					public void onAnimationStart(Animator animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						progressbar.setProgress(0);
						
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
						// TODO Auto-generated method stub
						
					}}, 1.0f, 1 * 1000);
				
				if(result == null){
					progressStatus.setVisibility(View.GONE);
					Toast.makeText(getActivity(), getActivity().getText(R.string.queue_error), Toast.LENGTH_LONG).show();
					enterQueue.setVisibility(View.VISIBLE);
					return;
				}
				
				remainingPerson = result.queueNumber - result.currentQueueNumber;
				queueNumber = result.queueNumber;
				singleStep = 1f / remainingPerson;
				currentStep = 0;
				startCheckingQueue();
				
				togo.setVisibility(View.VISIBLE);
				progressStatus.setTextSize(getResources().getDimension(R.dimen.very_large_text_size));
				progressStatus.setText(Integer.toString(remainingPerson));
				
			}

			@Override
			public void onPreExecuteCallBack() {
				animate(progressbar, null, 1.0f, 10 * 1000);
				progressStatus.setVisibility(View.VISIBLE);
				progressStatus.setText(R.string.queue_getting_queue);
			}};		
	}
	
	private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener,
			final float progress, final int duration) {

		progressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
		progressBarAnimator.setDuration(duration);

		progressBarAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
		if (listener != null) {
			progressBarAnimator.addListener(listener);
		}
		//progressBarAnimator.reverse();
		progressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
		progressBarAnimator.start();
	}
	
	private void startCheckingQueue(){
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        
        if(receiverListener == null){
        	receiverListener = new BroadcastReceiverListener<Void>() {
				
				@Override
				public void onReceive(Void result) {
					
					if(getQueueListener == null){
						getQueueListener = new AsyncTaskListener<Integer>(){

							@Override
							public void onPostExecuteCallBack(Integer result) {
								if(result < 0){
									return;
								}
								
								updateProgress(result);
							}

							@Override
							public void onPreExecuteCallBack() {
								
							}
						};
					}
					
					getQueueTask = new GetQueueAsyncTask(getQueueListener);
					getQueueTask.execute(queueNumber);
				}
			};
        }
        
        receiver = new TimeChangedReceiver(receiverListener);
        getActivity().registerReceiver(receiver, intentFilter);
	}
	
	private void updateProgress(int newRemainingPerson){
		if(newRemainingPerson == remainingPerson){
			return;
		}
		
		int steps = remainingPerson - newRemainingPerson;
		currentStep += (float)steps * singleStep; 
		
		remainingPerson = newRemainingPerson - 1;
		progressStatus.setText(Integer.toString(remainingPerson));
		animate(progressbar, null, currentStep, 1 * 1000);
	}
	
	private void toggleQueueState(boolean inQueue){
		
		if(inQueue){
			enterQueue.setVisibility(View.GONE);
			progressStatus.setTextSize(getResources().getDimension(R.dimen.very_large_text_size));
			progressStatus.setVisibility(View.VISIBLE);
			progressStatus.setText(Integer.toString(remainingPerson));
			togo.setVisibility(View.VISIBLE);
		} else {
			enterQueue.setVisibility(View.VISIBLE);
			progressStatus.setTextSize(getResources().getDimension(R.dimen.large_text_size));
			progressStatus.setVisibility(View.GONE);
			togo.setVisibility(View.GONE);
		}
	}
	
	private void createNotification(){
		PendingIntent intent = PendingIntent.getService(getActivity(), 0, new Intent(), 0);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				getActivity()).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getText(R.string.notification_title))
				.setContentText(getText(R.string.notification_summary))
				.addAction(R.drawable.ic_drawer, getText(R.string.action_give_way), intent)
				.addAction(R.drawable.ic_drawer, getText(R.string.action_leave_queue), intent);
		
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setSummaryText(getText(R.string.notification_summary));
		mBuilder.setStyle(bigTextStyle);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(getActivity(), MainActivity.class);

		// The stack builder object will contain an artificial back stack for the started Activity.
		// This ensures that navigating backward from the Activity leads out
		// of your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		
		// mId allows you to update the notification later on.
		Notification notification = mBuilder.build();
		notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(1, notification);
	}
}
