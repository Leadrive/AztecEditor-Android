/*
 * Copyright (C) 2016 Automattic
 * Copyright (C) 2015 Matthew Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wordpress.aztec.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.text.Spanned
import android.text.style.LineBackgroundSpan
import android.text.style.LineHeightSpan
import android.text.style.QuoteSpan
import android.text.style.UpdateLayout
import org.wordpress.aztec.AztecAttributes
import org.wordpress.aztec.formatting.BlockFormatter

class AztecQuoteSpan(
        override var nestingLevel: Int,
        override var attributes: AztecAttributes = AztecAttributes(),
        var quoteStyle: BlockFormatter.QuoteStyle = BlockFormatter.QuoteStyle(0, 0, 0f, 0, 0, 0, 0)
    ) : QuoteSpan(), LineBackgroundSpan, IAztecBlockSpan, LineHeightSpan, UpdateLayout {
    override var endBeforeBleed: Int = -1
    override var startBeforeCollapse: Int = -1

    val rect = Rect()

    override val TAG: String = "blockquote"

    override fun chooseHeight(text: CharSequence, start: Int, end: Int, spanstartv: Int, v: Int, fm: Paint.FontMetricsInt) {
        val spanned = text as Spanned
        val spanStart = spanned.getSpanStart(this)
        val spanEnd = spanned.getSpanEnd(this)

        if (start == spanStart || start < spanStart) {
            fm.ascent -= quoteStyle.verticalPadding
            fm.top -= quoteStyle.verticalPadding
        }
        if (end == spanEnd || spanEnd < end) {
            fm.descent += quoteStyle.verticalPadding
            fm.bottom += quoteStyle.verticalPadding
        }
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return quoteStyle.quoteMargin + quoteStyle.quoteWidth + quoteStyle.quotePadding
    }

    override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int,
                                   top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int,
                                   first: Boolean, layout: Layout) {
        val style = p.style
        val color = p.color

        p.style = Paint.Style.FILL
        p.color = quoteStyle.quoteColor
        c.drawRect(x.toFloat() + quoteStyle.quoteMargin, top.toFloat(),
                (x + quoteStyle.quoteMargin + dir * quoteStyle.quoteWidth).toFloat(), bottom.toFloat(), p)

        p.style = style
        p.color = color
    }

    override fun drawBackground(c: Canvas, p: Paint, left: Int, right: Int,
                                top: Int, baseline: Int, bottom: Int,
                                text: CharSequence?, start: Int, end: Int,
                                lnum: Int) {
        val alpha: Int = (quoteStyle.quoteBackgroundAlpha * 255).toInt()

        val paintColor = p.color
        p.color = Color.argb(
                alpha,
                Color.red(quoteStyle.quoteBackground),
                Color.green(quoteStyle.quoteBackground),
                Color.blue(quoteStyle.quoteBackground))
        rect.set(left + quoteStyle.quoteMargin, top, right, bottom)

        c.drawRect(rect, p)
        p.color = paintColor
    }
}
