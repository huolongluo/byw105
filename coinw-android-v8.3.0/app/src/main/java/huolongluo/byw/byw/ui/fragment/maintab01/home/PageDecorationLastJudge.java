package huolongluo.byw.byw.ui.fragment.maintab01.home;
public interface PageDecorationLastJudge {
    /**
     * Is the last row in one page
     * @param position
     * @return
     */
    boolean isLastRow(int position);
    /**
     * Is the last Colum in one row;
     * @param position
     * @return
     */
    boolean isLastColumn(int position);
    boolean isPageLast(int position);
}

