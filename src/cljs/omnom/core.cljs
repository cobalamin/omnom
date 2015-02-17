(ns omnom.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [goog.dom :as gdom]
            [ff-om-draggable.core :refer [draggable-item]]))

(def app-state
  (atom
   {:notes [{:position {:x 100
                        :y 50}
             :w 200
             :content "# This is cool\nTite *tite* __tite__\n```js\nfunction add(a, b) {\n  return a + b;\n}\n```"}
            {:position {:x 200
                        :y 300}
             :w 400
             :content "[Gooooogle](http://google.com)"}
            {:position {:x 500
                        :y 20}
             :w 200
             :content "# Head\n## lines\n### are cool"}]}))

(defn req-anim-frame
  [fn]
  (js/requestAnimationFrame fn))

(defn highlight!
  [node]
  (req-anim-frame
   #(let [pre-blocks (array-seq (gdom/getElementsByTagNameAndClass "pre" nil node))]
     (doall
      (map (fn [pre-block]
             (.highlightBlock js/hljs pre-block))
           pre-blocks)))))

(defn state-style
  [{:keys [dragging]}]
  (when dragging
    {:cursor "-webkit-grabbing"
     :-webkit-user-select "none"}))

(defcomponent note-view
  [note owner]

  (init-state
   [_]
   {:mounted false
    :dragging false})

  (did-mount
   [_]
   (om/set-state! owner :mounted true))

  (render
   [_]
   (let [{:keys [w h content]
          {:keys [x y]} :position} note]
     (when (om/get-state owner :mounted)
       (highlight! (om/get-node owner)))
     (dom/div {:class "note"
               :style (merge {:width w :top y :left x} (state-style (om/get-state owner)))
               :dangerouslySetInnerHTML {:__html (js/marked content)}
               :on-mouse-down #(om/set-state! owner :dragging true)
               :on-mouse-up #(om/set-state! owner :dragging false)}))))

(def draggable-note-view
  (draggable-item note-view [:position]))

(defcomponent root-view
  [app owner]

  (render
   [_]
   (dom/div
    (om/build-all draggable-note-view (:notes app)))))

(defn main []
  (om/root
    root-view
    app-state
    {:target (. js/document (getElementById "app"))}))
