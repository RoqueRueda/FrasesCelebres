package topicosBasesDatos.frases;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.database.Cursor;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewQAAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Cursor data;

	public NewQAAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	public NewQAAdapter(Context context, Cursor data) {
		this(context);
		this.data = data;
	}

	public void setData(Cursor data) {
		this.data = data;
	}

	public int getCount() {
		return data.getCount();
	}

	public Object getItem(int item) {
		if (data.move(item)) {
			ViewHolder holder = new ViewHolder();
			holder.mIdText.setText(data.getString(1));
			holder.mFraseText.setText(data.getString(2));
			holder.mAutorText.setText(data.getString(3));
			return holder;
		} else {
			return null;
		}
	}

	public long getItemId(int position) {
		if (data.move(position)) {
			return data.getLong(0);
		} else {
			return -1L;
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);

			holder = new ViewHolder();

			holder.mFraseText = (TextView) convertView
					.findViewById(R.id.frase_text);
			holder.mIdText = (TextView) convertView
					.findViewById(R.id.id_frase);
			holder.mAutorText = (TextView) convertView
					.findViewById(R.id.autor_text);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.mIdText.setText(data.getString(1));
		holder.mFraseText.setText(data.getString(2));
		holder.mAutorText.setText(data.getString(3));

		return convertView;
	}

	static class ViewHolder {
		TextView mIdText;
		TextView mAutorText;
		TextView mFraseText;
	}
}