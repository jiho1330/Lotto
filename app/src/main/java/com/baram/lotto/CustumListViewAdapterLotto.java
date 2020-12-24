package com.baram.lotto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.baram.lotto.model.LottoData;
import com.baram.lotto.model.LottoListItem;

import java.util.ArrayList;

public class CustumListViewAdapterLotto extends BaseAdapter{
    private ArrayList<LottoListItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public LottoListItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_lotto, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView CustomListViewLotto_Num1  = (ImageView) convertView.findViewById(R.id.lotto_img_num1) ;
        ImageView CustomListViewLotto_Num2  = (ImageView) convertView.findViewById(R.id.lotto_img_num2) ;
        ImageView CustomListViewLotto_Num3  = (ImageView) convertView.findViewById(R.id.lotto_img_num3) ;
        ImageView CustomListViewLotto_Num4  = (ImageView) convertView.findViewById(R.id.lotto_img_num4) ;
        ImageView CustomListViewLotto_Num5  = (ImageView) convertView.findViewById(R.id.lotto_img_num5) ;
        ImageView CustomListViewLotto_Num6  = (ImageView) convertView.findViewById(R.id.lotto_img_num6) ;
        ImageView CustomListViewLotto_Bonus = (ImageView) convertView.findViewById(R.id.lotto_img_Bonus) ;
        TextView CustomListViewLotto_Round = (TextView) convertView.findViewById(R.id.lotto_Round) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        LottoListItem myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        CustomListViewLotto_Num1.setImageDrawable(myItem.getNum1());
        CustomListViewLotto_Num2.setImageDrawable(myItem.getNum2());
        CustomListViewLotto_Num3.setImageDrawable(myItem.getNum3());
        CustomListViewLotto_Num4.setImageDrawable(myItem.getNum4());
        CustomListViewLotto_Num5.setImageDrawable(myItem.getNum5());
        CustomListViewLotto_Num6.setImageDrawable(myItem.getNum6());
        CustomListViewLotto_Bonus.setImageDrawable(myItem.getBonus());
        CustomListViewLotto_Round.setText(myItem.getRound());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */

        return convertView;
    }

    public void addItem(String Round, Drawable Num1, Drawable Num2, Drawable Num3, Drawable Num4, Drawable Num5, Drawable Num6, Drawable Bonus) {

        LottoListItem mItem = new LottoListItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setRound(Round + "회 : ");
        mItem.setNum1(Num1);
        mItem.setNum2(Num2);
        mItem.setNum3(Num3);
        mItem.setNum4(Num4);
        mItem.setNum5(Num5);
        mItem.setNum6(Num6);
        mItem.setBonus(Bonus);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);
    }
}
